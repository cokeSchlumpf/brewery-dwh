package simulation.entities.employee.state;

import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.EmployeeMessage;
import systems.brewery.BreweryManagementSystem;

@AllArgsConstructor(staticName = "apply")
public final class IdleState implements State {

    ActorContext<EmployeeMessage> ctx;

    BreweryManagementSystem bms;

    @Override
    public State onBrewABeerCommand(BrewABeerCommand cmd) {
        return BrewingState.apply(ctx, bms, cmd);
    }

}
