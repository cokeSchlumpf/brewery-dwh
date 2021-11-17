package simulation.entities.employee;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.EmployeeMessage;
import simulation.entities.employee.messages.ExecuteNextBrewingInstructionCommand;
import simulation.entities.employee.state.IdleState;
import simulation.entities.employee.state.State;
import systems.brewery.BreweryManagementSystem;

public final class Employee extends AbstractBehavior<EmployeeMessage> {

    private State state;

    public Employee(EmployeeContext ctx) {
        super(ctx.getActor());
        this.state = IdleState.apply(ctx);
    }

    public static Behavior<EmployeeMessage> create(String employeeName) {
        return Behaviors.setup(actor -> {
            var ctx = EmployeeContext.apply(actor, BreweryManagementSystem.apply(), employeeName);
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
            .build();
    }

}
