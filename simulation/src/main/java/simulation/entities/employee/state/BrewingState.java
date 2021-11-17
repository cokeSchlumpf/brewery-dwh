package simulation.entities.employee.state;

import akka.Done;
import akka.actor.typed.javadsl.AbstractBehavior;
import lombok.AllArgsConstructor;
import common.P;
import simulation.clock.Clock;
import simulation.entities.employee.EmployeeContext;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.EmployeeMessage;
import simulation.entities.employee.messages.ExecuteNextBrewingInstructionCommand;
import systems.brewery.values.instructions.*;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class BrewingState implements State {

    private final EmployeeContext ctx;

    private final List<Instruction> instructions;

    public static BrewingState apply(EmployeeContext ctx, BrewABeerCommand cmd) {
        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().startBrewing(cmd.getName(), "foo", 2))
            .waitFor(P.randomDuration(Duration.ofMinutes(1)))
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();

        var instructions = ctx.getBreweryManagementSystem().getRecipes().get(cmd.getName()).getInstructions();
        cmd.getAck().tell(Done.getInstance());
        return apply(ctx, instructions);
    }

    @Override
    public State onExecuteNextBrewingInstructionCommand(ExecuteNextBrewingInstructionCommand cmd) {
        if (instructions.isEmpty()) {
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

    private void addIngredient(AddIngredient instruction) {
        Clock
            .scheduler(ctx.getActor())
            .run(() -> {
                var ingredientProduct = P.randomItem(ctx
                    .getBreweryManagementSystem()
                    .getIngredientProducts()
                    .findByIngredientName(instruction.getIngredient().getName()));

                var amount = P.randomDouble(instruction.getAmount(), 10);

                ctx
                    .getBrewery()
                    .addIngredient(ingredientProduct.getProductName(), ingredientProduct.getProductName(), amount);
            })
            .waitFor(P.randomDuration(Duration.ofMinutes(2)), "adding ingredient takes some time")
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void boil(Boil instruction) {
        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBreweryManagementSystem().getBrewery().startBoiling())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(15)))
            .run(() -> ctx.getBrewery().stopBoiling())
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void mash(Mash instruction) {
        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().startMashing())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(5)))
            .run(() -> ctx.getBrewery().stopMashing())
            .schedule();
    }

    private void rest(Rest instruction) {
        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(3)))
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void sparge(Sparge instruction) {
        Clock
            .scheduler(ctx.getActor())
            .run(() -> ctx.getBrewery().startSparging())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(8)))
            .run(() -> ctx.getBrewery().stopSparging())
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

}
