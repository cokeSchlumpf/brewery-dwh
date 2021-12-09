package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.customer.messages.CustomerMessage;

@Value
@AllArgsConstructor(staticName = "apply")
public class CheckBeerSupply implements EmployeeMessage{
    ActorRef<Done> ack;
    ActorRef<CheckBeerSupplyResponse> response;
}
