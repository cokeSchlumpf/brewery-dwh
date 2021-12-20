package simulation.entities.employee.brewing.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import simulation.entities.employee.brewing.BrewingEmployee;

import java.time.Duration;
import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class CheckMashTemperature implements BrewingEmployee.Message {

    Instant started;

    double startTemperature;

    double endTemperature;

    Duration remainingDuration;

    ActorRef<Done> ack;

}
