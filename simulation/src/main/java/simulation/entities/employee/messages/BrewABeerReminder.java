package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.Employee;

@Value
@AllArgsConstructor(staticName = "apply")
public class BrewABeerReminder implements Employee.Message {

    String beerKey;

    ActorRef<Done> ack;

}
