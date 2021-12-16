package simulation.entities.employee.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.customer.messages.CustomerMessage;
import simulation.entities.customer.messages.ReceiveBeerFromOnlineStore;
import systems.sales.values.Order;

@Value
@AllArgsConstructor(staticName = "apply")
public class PrepareOrderToShipCommand implements EmployeeMessage{
    ActorRef<Done> ack;
    Order order;
    ActorRef<CustomerMessage> recipient;
}
