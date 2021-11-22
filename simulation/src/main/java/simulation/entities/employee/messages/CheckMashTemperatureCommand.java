package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class CheckMashTemperatureCommand implements EmployeeMessage{

    double end_temp;

    ActorRef<Done> ack;

}
