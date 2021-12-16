package simulation.entities.employee.state;

import akka.Done;
import simulation.entities.employee.messages.*;

public interface State {

    default State onBrewABeerCommand(BrewABeerCommand cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onCheckBeerSupplyCommand(CheckBeerSupply cmd) {
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onCheckBeerSupplyCommandResponse(CheckBeerSupplyResponse cmd){
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

    default State onBottlingBrewCommand(BottlingBrewCommand cmd){
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onBeerOrderCommand(BeerOrderCommand cmd){
        cmd.getAck().tell(Done.getInstance());
        return this;
    }

    default State onPrepareOrderToShipCommand(PrepareOrderToShipCommand cmd){
        cmd.getAck().tell(Done.getInstance());
        return this;
    }
}
