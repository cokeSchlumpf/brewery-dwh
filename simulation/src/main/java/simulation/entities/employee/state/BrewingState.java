package simulation.entities.employee.state;

import akka.Done;
import common.P;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import simulation.clock.Clock;
import simulation.entities.brewery.values.HeatingLevel;
import simulation.entities.employee.EmployeeContext;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.CheckHeatingTemperatureCommand;
import simulation.entities.employee.messages.CheckMashTemperatureCommand;
import simulation.entities.employee.messages.ExecuteNextBrewingInstructionCommand;
import systems.brewery.values.Brew;
import systems.brewery.values.event.*;
import systems.brewery.values.instructions.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class BrewingState implements State {

    private final EmployeeContext ctx;

    private final List<Instruction> instructions;

    public static BrewingState apply(EmployeeContext ctx, BrewABeerCommand cmd) {
        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().prepareBrew())
            .waitFor(P.randomDuration(Duration.ofMinutes(1)))
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();


        var recipe = ctx
            .getBreweryManagementSystem()
            .getRecipes()
            .getRecipeByName(cmd.getName());

        var instructions = recipe.getInstructions();

        var brew = Brew.apply(
            recipe, ctx.getEmployee(), Clock.getInstance().getNowAsInstant(), P.randomDouble(8.0, 1.2));

        cmd.getAck().tell(Done.getInstance());
        ctx.getBreweryManagementSystem().getBrews().insertBrew(brew);
        ctx.log("Start brewing beer `%s`", brew.getBeer().getBeerKey());

        return apply(ctx, instructions);
    }

    @Override
    public State onExecuteNextBrewingInstructionCommand(ExecuteNextBrewingInstructionCommand cmd) {
        if (instructions.isEmpty()) {
            ctx.getBrewery().finish();
            ctx.getBreweryManagementSystem().getBrews().updateBrew(Clock.getInstance().getNowAsInstant(), P.randomDouble(14.0, 1.2));
            ctx.log("Finished Brewing! Post!");

            cmd.getAck().tell(Done.getInstance());
            return this;
        }

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

        return this;
    }

    @Override
    public State onCheckMashTemperatureCommand(CheckMashTemperatureCommand cmd) {
        double currentTemperature = ctx.getBrewery().readTemperature();

        if (currentTemperature >= cmd.getEndTemperature()) {
            Clock
                .scheduler(ctx.getActor())
                .run(now -> {
                    var mashed = Mashed.apply(cmd.getStarted(), now, cmd.getStartTemperature(), currentTemperature);

                    ctx.getBrewery().stopMashing();
                    ctx.getBrewery().setHeating(HeatingLevel.L00_OFF);
                    ctx.getBreweryManagementSystem().getBrews().logBrewEvent(mashed);

                    ctx.log(
                        "Mashed beer for %s minutes to temperature %s",
                        ChronoUnit.MINUTES.between(cmd.getStarted(), now), currentTemperature);
                })
                .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
                .schedule();
        } else {
            var checkAgainAfter = P.randomDuration(cmd.getRemainingDuration(), Duration.ofMinutes(5));
            var remainingMashingDuration = checkAgainAfter.minus(checkAgainAfter);
            if (remainingMashingDuration.isNegative()) remainingMashingDuration = Duration.ofMinutes(3);

            var remainingMashingDurationFinal = remainingMashingDuration;

            Clock
                .scheduler(ctx.getActor())
                .run(() -> {
                    var level = getHeatingLevel(currentTemperature, cmd.getEndTemperature(), remainingMashingDurationFinal);

                    if (!level.equals(ctx.getBrewery().getHeatingLevel())) {
                        ctx.getBrewery().setHeating(level);
                    }
                })
                .waitFor(checkAgainAfter)
                .sendMessage(ref -> cmd.withRemainingDuration(remainingMashingDurationFinal))
                .schedule();
        }

        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    private void addIngredient(AddIngredient instruction) {
        Clock
            .scheduler(ctx.getActor())
            .run(now -> {
                var ingredientProduct = P.randomItem(ctx
                    .getBreweryManagementSystem()
                    .getIngredientProducts()
                    .findIngredientProductByIngredientName(instruction.getIngredient().getName()));

                var amount = P.randomDouble(instruction.getAmount(), 10);

                ctx
                    .getBrewery()
                    .addIngredient(ingredientProduct, amount);

                ctx
                    .getBreweryManagementSystem()
                    .getBrews()
                    .logBrewEvent(IngredientAdded.apply(now, ingredientProduct, amount));

                ctx.log(
                    "Added ingredient %s (%.2f%s)",
                    instruction.getIngredient().getName(), instruction.getAmount(),
                    instruction.getIngredient().getUnit());
            })
            .waitFor(P.randomDuration(Duration.ofMinutes(2)), "adding ingredient takes some time")
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void boil(Boil instruction) {
        var started = Clock.getInstance().getNowAsInstant();

        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().setHeating(HeatingLevel.L09_HIGH_HEAT))
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(5)))
            .run(now -> {
                ctx.getBrewery().setHeating(HeatingLevel.L00_OFF);
                ctx.getBreweryManagementSystem().getBrews().logBrewEvent(Boiled.apply(started, now));
                ctx.log("Boiled beer for %s minutes", instruction.getDuration().toMinutes());
            })
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void mash(Mash instruction) {
        var toleranceRange = 5;
        var currentTemperature = ctx.getBrewery().readTemperature();

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
                .scheduler(ctx.getActor())
                .run(() -> {
                    var level = getHeatingLevel(
                        currentTemperature, instruction.getEndTemperature(), instruction.getDuration());
                    ctx.getBrewery().startMashing();
                    ctx.getBrewery().setHeating(level);
                    ctx.log("Started mashing and set brewery heating level to %s", level);
                })
                .waitFor(checkAgainAfter)
                .sendMessage((now, ack) -> CheckMashTemperatureCommand.apply(
                    now, currentTemperature, instruction.getEndTemperature(), remainingMashingDuration, ack))
                .schedule();
        } else {
            /*
             * Start boiling w/o mashing first.
             */
            Clock
                .scheduler(ctx.getActor())
                .run(() -> {
                    var level = HeatingLevel.L08_MEDIUM_HIGH_HEAT;
                    ctx.getBrewery().setHeating(level);
                    ctx.log("Set brewery heating level to %s to warm beer to mashing temperature.", level);
                })
                .waitFor(P.randomDuration(Duration.ofMinutes(2)))
                .sendMessage((now, ack) -> CheckHeatingTemperatureCommand.apply(instruction, now, ack))
                .schedule();
        }
    }

    private void rest(Rest instruction) {
        var duration = P.randomDuration(instruction.getDuration(), Duration.ofMinutes(3));
        var started = Clock.getInstance().getNowAsInstant();

        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().setHeating(HeatingLevel.L00_OFF))
            .waitFor(duration)
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .run(now -> {
                ctx.getBreweryManagementSystem().getBrews().logBrewEvent(Rested.apply(started, now));
                ctx.log("Rested beer for %s", duration);
            })
            .schedule();
    }

    private void sparge(Sparge instruction) {
        var duration = P.randomDuration(instruction.getDuration(), Duration.ofMinutes(8));
        var started = Clock.getInstance().getNowAsInstant();

        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().startSparging())
            .waitFor(duration)
            .run(now -> {
                ctx.getBrewery().stopSparging();
                ctx.getBreweryManagementSystem().getBrews().logBrewEvent(Sparged.apply(started, now));
                ctx.log("Sparged beer for %s", duration);
            })
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private HeatingLevel getHeatingLevel(double startTemperature, double endTemperature, Duration duration) {
        return Arrays
            .stream(HeatingLevel.values())
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
                return 0;
        }
    }

}
