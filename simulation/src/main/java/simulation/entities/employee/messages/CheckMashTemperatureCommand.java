package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import simulation.entities.brewery.values.HeatingLevel;

import java.time.Duration;
import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class CheckMashTemperatureCommand implements EmployeeMessage{

    Instant started;

    double startTemperature;

    double endTemperature;

    Duration remainingDuration;

    ActorRef<Done> ack;

}
