package simulation.entities.employee.brewing.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import simulation.entities.employee.brewing.BrewingEmployee;
import systems.brewery.values.instructions.Mash;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class CheckHeatingTemperature implements BrewingEmployee.Message {

    Mash instruction;

    Instant started;

    ActorRef<Done> ack;

}
