package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.messages.PlaceOrder;

@Value
@AllArgsConstructor(staticName = "apply")
public class ReceiveBeerOrderConfirmation implements Message {

    PlaceOrder.PlaceOrderResponse response;

    ActorRef<Done> ack;

}
