package simulation.entities.employee.state;

import lombok.AllArgsConstructor;
import simulation.entities.employee.EmployeeContext;
import simulation.entities.employee.messages.BrewABeerCommand;

@AllArgsConstructor(staticName = "apply")
public final class IdleState implements State {

    private final EmployeeContext ctx;

    @Override
    public State onBrewABeerCommand(BrewABeerCommand cmd) {
        return BrewingState.apply(ctx, cmd);
    }

}
