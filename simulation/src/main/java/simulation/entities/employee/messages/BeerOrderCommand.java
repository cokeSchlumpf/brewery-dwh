package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import systems.sales.values.Order;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class BeerOrderCommand implements EmployeeMessage{
    ActorRef<Done> ack;
    Order order;
    ActorRef<SendBeerCommand> response;
}
