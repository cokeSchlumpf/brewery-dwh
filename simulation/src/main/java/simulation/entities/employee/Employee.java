package simulation.entities.employee;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
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

    public Employee(ActorContext<EmployeeMessage> context) {
        super(context);
        this.state = IdleState.apply(context, BreweryManagementSystem.apply());
    }

    public static Behavior<EmployeeMessage> create() {
        return Behaviors.setup(Employee::new);
    }

    @Override
    public Receive<EmployeeMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(BrewABeerCommand.class, cmd -> {
                this.state = state.onBrewABeerCommand(cmd);
                return this;
            })
            .onMessage(ExecuteNextBrewingInstructionCommand.class, cmd -> {
                this.state = state.onExecuteNextBrewingInstructionCommand(cmd);
                return this;
            })
            .build();
    }

}
