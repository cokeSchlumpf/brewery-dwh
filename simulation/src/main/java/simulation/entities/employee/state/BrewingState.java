package simulation.entities.employee.state;

import akka.Done;
import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import simulation.P;
import simulation.clock.Clock;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.EmployeeMessage;
import simulation.entities.employee.messages.ExecuteNextBrewingInstructionCommand;
import systems.brewery.BreweryManagementSystem;
import systems.brewery.values.instructions.*;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class BrewingState implements State {

    private final ActorContext<EmployeeMessage> ctx;

    private final BreweryManagementSystem bms;

    private final List<Instruction> instructions;

    public static BrewingState apply(ActorContext<EmployeeMessage> ctx, BreweryManagementSystem bms,
                                     BrewABeerCommand cmd) {
        Clock
            .scheduler(ctx)
            .run(() -> bms.getBrewery().startBrewing(cmd.getName(), "foo", 2))
            .waitFor(P.randomDuration(Duration.ofMinutes(1)), "begin-brewing")
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();

        var instructions = bms.getRecipes().get(cmd.getName()).getInstructions();
        cmd.getAck().tell(Done.getInstance());
        return apply(ctx, bms, instructions);
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
            .scheduler(ctx)
            .run(() -> {
                var ingredient = P.randomItem(bms
                    .getIngredientProducts()
                    .findByIngredientName(instruction.getIngredient().getName()));

                var amount = P.randomDouble(instruction.getAmount(), 10);

                bms
                    .getBrewery()
                    .addIngredient(ingredient.getProductName(), ingredient.getProductName(), amount);
            })
            .waitFor(P.randomDuration(Duration.ofMinutes(2)), "adding ingredient takes some time.")
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void boil(Boil instruction) {
        Clock
            .scheduler(ctx)
            .run(() -> bms.getBrewery().startBoiling())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(15)))
            .run(() -> bms.getBrewery().stopBoiling())
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void mash(Mash instruction) {
        Clock
            .scheduler(ctx)
            .run(() -> bms.getBrewery().startMashing())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(5)))
            .run(() -> bms.getBrewery().stopMashing())
            .schedule();
    }

    private void rest(Rest instruction) {
        Clock
            .scheduler(ctx)
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(3)))
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

    private void sparge(Sparge instruction) {
        Clock
            .scheduler(ctx)
            .run(() -> bms.getBrewery().startSparging())
            .waitFor(P.randomDuration(instruction.getDuration(), Duration.ofMinutes(8)))
            .run(() -> bms.getBrewery().stopSparging())
            .sendMessage(ExecuteNextBrewingInstructionCommand::apply)
            .schedule();
    }

}
