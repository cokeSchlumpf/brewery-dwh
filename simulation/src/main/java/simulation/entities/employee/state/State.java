package simulation.entities.employee.state;

import akka.Done;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.CheckHeatingTemperatureCommand;
import simulation.entities.employee.messages.ExecuteNextBrewingInstructionCommand;
import simulation.entities.employee.messages.CheckMashTemperatureCommand;

public interface State {

    default State onBrewABeerCommand(BrewABeerCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onExecuteNextBrewingInstructionCommand(ExecuteNextBrewingInstructionCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onCheckMashTemperatureCommand(CheckMashTemperatureCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onCheckHeatingTemperatureCommand(CheckHeatingTemperatureCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

}
