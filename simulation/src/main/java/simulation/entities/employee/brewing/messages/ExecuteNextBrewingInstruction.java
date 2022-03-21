package simulation.entities.employee.brewing.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.brewing.BrewingEmployee;

@Value
@AllArgsConstructor(staticName = "apply")
public class ExecuteNextBrewingInstruction implements BrewingEmployee.Message {

    ActorRef<Done> ack;

}
