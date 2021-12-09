package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.brewery.values.BottleSize;

@Value
@AllArgsConstructor(staticName = "apply")
public class BottlingBrewCommand implements EmployeeMessage{

    ActorRef<Done> ack;
    BottleSize bottleSize;

}
