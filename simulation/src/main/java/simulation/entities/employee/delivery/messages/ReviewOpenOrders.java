package simulation.entities.employee.delivery.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.delivery.DeliveringEmployee;
import systems.sales.values.Order;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ReviewOpenOrders implements DeliveringEmployee.Message {

    List<Order> openOrders;

    ActorRef<Done> ack;

}
