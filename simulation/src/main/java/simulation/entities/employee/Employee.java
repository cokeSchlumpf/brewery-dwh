package simulation.entities.employee;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.apache.commons.lang3.concurrent.CircuitBreakingException;
import simulation.entities.brewery.Brewery;
import simulation.entities.employee.messages.*;
import simulation.entities.employee.state.IdleState;
import simulation.entities.employee.state.State;
import systems.brewery.BreweryManagementSystem;
import systems.sales.SalesManagementSystem;

public final class Employee extends AbstractBehavior<EmployeeMessage> {

    private State state;

    public Employee(EmployeeContext ctx) {
        super(ctx.getActor());
        this.state = IdleState.apply(ctx);
    }

    public static Behavior<EmployeeMessage> create(BreweryManagementSystem bms, SalesManagementSystem sms, systems.reference.model.Employee employee, Brewery brewery) {
        return Behaviors.setup(actor -> {
            var ctx = EmployeeContext.apply(actor, bms, sms,brewery, employee);
            return new Employee(ctx);
        });
    }

    @Override
    public Receive<EmployeeMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(BrewABeerCommand.class, cmd -> {
                this.state = state.onBrewABeerCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(ExecuteNextBrewingInstructionCommand.class, cmd -> {
                this.state = state.onExecuteNextBrewingInstructionCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(CheckMashTemperatureCommand.class, cmd -> {
                this.state = state.onCheckMashTemperatureCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(CheckHeatingTemperatureCommand.class, cmd -> {
                this.state = state.onCheckHeatingTemperatureCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(CheckBeerSupply.class, cmd -> {
                this.state = state.onCheckBeerSupplyCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(BeerOrderCommand.class, cmd -> {
                this.state = state.onBeerOrderCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(BottlingBrewCommand.class, cmd -> {
                this.state = state.onBottlingBrewCommand(cmd);
                return Behaviors.same();
            })
            .onMessage(PutBrewIntoStorageCommand.class, cmd -> {
                this.state = state.onPutBrewIntoStorageCommand(cmd);
                return Behaviors.same();
            })
            .build();
    }

}
