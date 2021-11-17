package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class BrewABeerCommand implements EmployeeMessage {

    String name;

    ActorRef<Done> ack;

}
