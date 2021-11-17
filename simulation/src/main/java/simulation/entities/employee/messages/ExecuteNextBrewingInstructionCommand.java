package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class ExecuteNextBrewingInstructionCommand implements EmployeeMessage {

    ActorRef<Done> ack;

}
