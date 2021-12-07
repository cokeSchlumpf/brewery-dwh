package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.values.instructions.Mash;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class CheckHeatingTemperatureCommand implements EmployeeMessage {

    Mash instruction;

    Instant started;

    ActorRef<Done> ack;

}
