package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.brewery.values.BottleSize;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class BottlingBrewCommand implements EmployeeMessage{

    ActorRef<Done> ack;

    /**
     * End time of brewing
     */
    Instant brewing_time;

    /**
     * Beer that was brewed.
     */
    String beer_id;

    /**
     * Amount of beer that was brewed (in liters)
     */
    Double  volume;

}
