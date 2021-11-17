package simulation.entities.employee.state;

import akka.Done;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.ExecuteNextBrewingInstructionCommand;

public interface State {

    default State onBrewABeerCommand(BrewABeerCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onExecuteNextBrewingInstructionCommand(ExecuteNextBrewingInstructionCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

}
