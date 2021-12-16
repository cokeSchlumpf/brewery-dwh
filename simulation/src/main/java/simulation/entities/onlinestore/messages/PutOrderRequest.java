package simulation.entities.onlinestore.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.customer.messages.CustomerMessage;
import simulation.entities.customer.messages.ReceiveBeerFromOnlineStore;
import systems.sales.values.Order;

@Value
@AllArgsConstructor(staticName = "apply")
public class PutOrderRequest implements OnlineStoreMessage{
    Order order;
    ActorRef<CustomerMessage> recipient;
    ActorRef<Done> ack;
}
