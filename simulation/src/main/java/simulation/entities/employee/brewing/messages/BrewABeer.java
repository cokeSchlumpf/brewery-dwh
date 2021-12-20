package simulation.entities.employee.brewing.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import simulation.entities.employee.brewing.BrewingEmployee;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class BrewABeer implements BrewingEmployee.Message {

    String name;

    ActorRef<Done> ack;

}
