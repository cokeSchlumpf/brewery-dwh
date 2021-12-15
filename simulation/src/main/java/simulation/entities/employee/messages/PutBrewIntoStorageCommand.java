package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.values.Bottling;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class PutBrewIntoStorageCommand implements EmployeeMessage{
    List<Bottling> bottlings;
    ActorRef<Done> ack;

}
