package simulation.entities.employee.brewing;

import akka.Done;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import common.P;
import org.apache.commons.lang3.tuple.Pair;
import simulation.clock.Clock;
import simulation.entities.brewery.Brewery;
import simulation.entities.brewery.values.HeatingLevel;
import simulation.entities.employee.brewing.messages.BrewABeer;
import simulation.entities.employee.brewing.messages.CheckHeatingTemperature;
import simulation.entities.employee.brewing.messages.CheckMashTemperature;
import simulation.entities.employee.brewing.messages.ExecuteNextBrewingInstruction;
import systems.brewery.BreweryManagementSystem;
import systems.brewery.values.Brew;
import systems.brewery.values.event.*;
import systems.brewery.values.instructions.*;
import systems.reference.model.Employee;
import systems.sales.SalesManagementSystem;
import systems.sales.values.Bottling;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public final class BrewingEmployee extends AbstractBehavior<BrewingEmployee.Message> {

    public interface Message {
    }

    private final Employee employee;

    private final BreweryManagementSystem bms;

    private final SalesManagementSystem sms;

    private final Brewery brewery;

    private final Queue<BrewABeer> brewABeerQueue;

    private List<Instruction> instructions;

    private BrewABeer currentBrew;

    public BrewingEmployee(ActorContext<Message> context, Employee employee, BreweryManagementSystem bms,
                           SalesManagementSystem sms, Brewery brewery) {
        super(context);
        this.employee = employee;
        this.bms = bms;
        this.sms = sms;
        this.brewery = brewery;

        this.brewABeerQueue = Queues.newLinkedBlockingQueue();
        this.instructions = Lists.newArrayList();
        this.currentBrew = null;
    }

    public static Behavior<Message> create(Employee employee, BreweryManagementSystem bms, SalesManagementSystem sms, Brewery brewery) {
        return Behaviors.setup(ctx -> new BrewingEmployee(ctx, employee, bms, sms, brewery));
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(BrewABeer.class, cmd -> {
                onBrewABeerCommand(cmd);
                return this;
            })
            .onMessage(CheckHeatingTemperature.class, cmd -> {
                onCheckHeatingTemperatureCommand(cmd);
                return this;
            })
            .onMessage(CheckMashTemperature.class, cmd -> {
                onCheckMashTemperatureCommand(cmd);
                return this;
            })
            .onMessage(ExecuteNextBrewingInstruction.class, cmd -> {
                onExecuteNextBrewingInstructionCommand(cmd);
                return this;
            })
            .build();
    }

    private void onBrewABeerCommand(BrewABeer cmd) {
        if (currentBrew != null) {
            this.brewABeerQueue.offer(cmd);
            cmd.getAck().tell(Done.getInstance());
        } else {
            var recipe = this.bms
                .getRecipes()
                .getRecipeByName(cmd.getName());

            var brew = Brew.apply(
                recipe, this.employee, Clock.getInstance().getNowAsInstant(), P.randomDouble(8.0, 1.2));

            this.bms.getBrews().insertBrew(brew);
            this.instructions = recipe.getInstructions();
            this.currentBrew = cmd;

            log("Start brewing beer `{}`", brew.getBeer().getBeerKey());

            Clock
                .scheduler(this.getContext())
                .run(this.brewery::prepareBrew)
                .waitFor(P.randomDuration(Duration.ofMinutes(1)))
                .ask(ExecuteNextBrewingInstruction::apply)
                .scheduleAndAcknowledge(cmd.getAck());
        }
    }

    private void onCheckHeatingTemperatureCommand(CheckHeatingTemperature cmd) {
        double currentTemperature = brewery.readTemperature();

        if (currentTemperature < cmd.getInstruction().getStartTemperature()) {
            Clock
                .scheduler(getContext())
                .run(() -> {
                    var level = HeatingLevel.L08_MEDIUM_HIGH_HEAT;
                    brewery.setHeating(level);
                    log("Set brewery heating level to %s to warm beer to mashing temperature.", level);
                })
                .waitFor(P.randomDuration(Duration.ofMinutes(2)))
                .ask((now, ack) -> cmd.withAck(ack))
                .scheduleAndAcknowledge(cmd.getAck());
        } else {
            mash(cmd.getInstruction());
            cmd.getAck().tell(Done.getInstance());
        }
    }

    private void onCheckMashTemperatureCommand(CheckMashTemperature cmd) {
        double currentTemperature = brewery.readTemperature();

        if (currentTemperature >= cmd.getEndTemperature()) {
            Clock
                .scheduler(getContext())
                .run(now -> {
                    var mashed = Mashed.apply(cmd.getStarted(), now, cmd.getStartTemperature(), currentTemperature);

                    brewery.stopMashing();
                    brewery.setHeating(HeatingLevel.L00_OFF);
                    bms.getBrews().logBrewEvent(mashed);

                    log(
                        "Mashed beer for %s minutes to temperature %s",
                        ChronoUnit.MINUTES.between(cmd.getStarted(), now), currentTemperature);
                })
                .ask(ExecuteNextBrewingInstruction::apply)
                .scheduleAndAcknowledge(cmd.getAck());
        } else {
            var checkAgainAfter = P.randomDuration(cmd.getRemainingDuration(), Duration.ofMinutes(5));
            var remainingMashingDuration = checkAgainAfter.minus(checkAgainAfter);
            if (remainingMashingDuration.isNegative()) remainingMashingDuration = Duration.ofMinutes(3);

            var remainingMashingDurationFinal = remainingMashingDuration;

            Clock
                .scheduler(getContext())
                .run(() -> {
                    var level = getHeatingLevel(currentTemperature, cmd.getEndTemperature(),
                        remainingMashingDurationFinal);

                    if (!level.equals(brewery.getHeatingLevel())) {
                        brewery.setHeating(level);
                    }
                })
                .waitFor(checkAgainAfter)
                .ask(ref -> cmd.withRemainingDuration(remainingMashingDurationFinal))
                .scheduleAndAcknowledge(cmd.getAck());
        }
    }

    private void onExecuteNextBrewingInstructionCommand(ExecuteNextBrewingInstruction cmd) {
        if (instructions.isEmpty()) {
            brewery.finish();
            bms.getBrews().updateBrew(Clock.getInstance().getNowAsInstant(), P.randomDouble(14.0, 1.2));
            log("Finished Brewing! Post!");

            scheduleBottling();
            currentBrew = null;

            if (brewABeerQueue.isEmpty()) {
                cmd.getAck().tell(Done.getInstance());
            } else {
                var next = brewABeerQueue.poll().withAck(cmd.getAck());
                onBrewABeerCommand(next);
            }
        } else {
            var nextInstruction = instructions.get(0);

            if (nextInstruction instanceof AddIngredient) {
                addIngredient((AddIngredient) nextInstruction);
            } else if (nextInstruction instanceof Boil) {
                boil((Boil) nextInstruction);
            } else if (nextInstruction instanceof Mash) {
                mash((Mash) nextInstruction);
            } else if (nextInstruction instanceof Rest) {
                rest((Rest) nextInstruction);
            } else if (nextInstruction instanceof Sparge) {
                sparge((Sparge) nextInstruction);
            } else {
                throw new RuntimeException(String.format("Unknown instruction type: %s", nextInstruction.getClass()));
            }

            cmd.getAck().tell(Done.getInstance());
            this.instructions.remove(0);
        }
    }

    private void addIngredient(AddIngredient instruction) {
        Clock
            .scheduler(getContext())
            .run(now -> {
                var ingredientProduct = P.randomItem(bms
                    .getIngredientProducts()
                    .findIngredientProductByIngredientName(instruction.getIngredient().getName()));

                var amount = P.randomDouble(instruction.getAmount(), 10);

                brewery.addIngredient(ingredientProduct, amount);
                bms.getBrews().logBrewEvent(IngredientAdded.apply(now, ingredientProduct, amount));

                log(
                    "Added ingredient %s (%.2f%s)",
                    instruction.getIngredient().getName(), instruction.getAmount(),
                    instruction.getIngredient().getUnit());
            })
            .waitFor(P.randomDuration(Duration.ofMinutes(2)), "adding ingredient takes some time")
            .ask(ExecuteNextBrewingInstruction::apply)
            .schedule();
    }

    private void boil(Boil instruction) {
        var started = Clock.getInstance().getNowAsInstant();

        Clock
            .scheduler(getContext())
            .run(() -> brewery.setHeating(HeatingLevel.L09_HIGH_HEAT))
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(5)))
            .run(now -> {
                brewery.setHeating(HeatingLevel.L00_OFF);
                bms.getBrews().logBrewEvent(Boiled.apply(started, now));
                log("Boiled beer for %s minutes", instruction.getDuration().toMinutes());
            })
            .ask(ExecuteNextBrewingInstruction::apply)
            .schedule();
    }

    private void log(String message, Object... args) {
        getContext().getLog()
            .info(String.format("%s -- %s -- %s", Clock.getInstance()
                .getNow(), employee.getId(), String.format(message, args)));
    }

    private void scheduleBottling() {
        var timeOfBrewing = Clock.getInstance().getNowAsInstant();
        var bestBeforeDate = timeOfBrewing.plus(Duration.ofDays(7 * 31));
        var volume = 5000d;

        Clock
            .scheduler(getContext())
            .waitFor(P.randomDuration(Duration.ofDays(3), Duration.ofDays(2)))
            .run(now -> {
                var products = sms
                    .getProducts()
                    .getProductsByBeerId(currentBrew.getName());

                products.forEach(product -> {
                    var productVolume = Math.round(volume / products.size());
                    var bottlesCount = Math.toIntExact(Math.round(productVolume / product.getVolume()));
                    var bottling = Bottling.apply(product, now, bestBeforeDate, bottlesCount);
                    sms.getProducts().insertBottling(bottling);
                    sms.getProducts().addToStock(product, bottlesCount);

                    log("Added `%d` bottles of `%s` to stock.", bottlesCount, product.getProductName());
                });
            })
            .schedule();
    }

    private void mash(Mash instruction) {
        var toleranceRange = 5;
        var currentTemperature = brewery.readTemperature();

        /*
         * Check current temperature.
         */
        if (Math.abs(instruction.getStartTemperature() - currentTemperature) < toleranceRange || instruction.getStartTemperature() < currentTemperature) {
            /*
             * Mashing can start immediately.
             */
            var checkAgainAfter = P.randomDuration(instruction.getDuration(), Duration.ofMinutes(5));
            var remainingMashingDuration = checkAgainAfter.minus(checkAgainAfter);

            Clock
                .scheduler(getContext())
                .run(() -> {
                    var level = getHeatingLevel(
                        currentTemperature, instruction.getEndTemperature(), instruction.getDuration());
                    brewery.startMashing();
                    brewery.setHeating(level);
                    log("Started mashing and set brewery heating level to %s, currentTemperature %.2f °C, target temperature: %.2f °C", level, currentTemperature, instruction.getEndTemperature());
                })
                .waitFor(checkAgainAfter)
                .ask((now, ack) -> CheckMashTemperature.apply(
                    now, currentTemperature, instruction.getEndTemperature(), remainingMashingDuration, ack))
                .schedule();
        } else {
            /*
             * Start boiling w/o mashing first.
             */
            Clock
                .scheduler(getContext())
                .run(() -> {
                    var level = HeatingLevel.L08_MEDIUM_HIGH_HEAT;
                    brewery.setHeating(level);
                    log("Set brewery heating level to %s to warm beer to mashing temperature.", level);
                })
                .waitFor(P.randomDuration(Duration.ofMinutes(2)))
                .ask((now, ack) -> CheckHeatingTemperature.apply(instruction, now, ack))
                .schedule();
        }
    }

    private void rest(Rest instruction) {
        var duration = P.randomDuration(instruction.getDuration(), Duration.ofMinutes(3));
        var started = Clock.getInstance().getNowAsInstant();

        Clock
            .scheduler(getContext())
            .run(() -> brewery.setHeating(HeatingLevel.L00_OFF))
            .waitFor(duration)
            .ask(ExecuteNextBrewingInstruction::apply)
            .run(now -> {
                bms.getBrews().logBrewEvent(Rested.apply(started, now));
                log("Rested beer for %s", duration);
            })
            .schedule();
    }

    private void sparge(Sparge instruction) {
        var duration = P.randomDuration(instruction.getDuration(), Duration.ofMinutes(8));
        var started = Clock.getInstance().getNowAsInstant();

        Clock
            .scheduler(getContext())
            .run(brewery::startSparging)
            .waitFor(duration)
            .run(now -> {
                brewery.stopSparging();
                bms.getBrews().logBrewEvent(Sparged.apply(started, now));
                log("Sparged beer for %s", duration);
            })
            .ask(ExecuteNextBrewingInstruction::apply)
            .schedule();
    }

    private HeatingLevel getHeatingLevel(double startTemperature, double endTemperature, Duration duration) {
        return Arrays
            .stream(HeatingLevel.values())
            .filter(level -> !level.equals(HeatingLevel.L00_OFF))
            .map(level -> Pair.of(level, getExpectedHeatingDuration(level, startTemperature, endTemperature)))
            .map(p -> Pair.of(p.getLeft(), Math.abs(duration.minus(p.getRight()).getSeconds())))
            .min(Comparator.comparing(Pair::getRight))
            .map(Pair::getLeft)
            .orElseThrow();
    }

    private Duration getExpectedHeatingDuration(HeatingLevel level, double startTemperature, double endTemperature) {
        double diff = endTemperature - startTemperature;

        if (diff < 0) {
            return Duration.ZERO;
        } else {
            return Duration.ofMinutes(Math.round(diff / getExpectedHeatingSlope(level)));
        }
    }

    private double getExpectedHeatingSlope(HeatingLevel level) {
        switch (level) {
            case L01_LOWEST_HEAT:
                return 0.4;
            case L02_VERY_LOW_HEAT:
                return 0.5;
            case L03_LOW_HEAT:
                return 1.3;
            case L04_MEDIUM_LOW_HEAT:
                return 3;
            case L07_MEDIUM_HEAT:
                return 5;
            case L08_MEDIUM_HIGH_HEAT:
                return 10;
            case L09_HIGH_HEAT:
                return 15;
            default:
                throw new RuntimeException(String.format("This method should not be called for %s",
                    HeatingLevel.L00_OFF));
        }
    }

}
