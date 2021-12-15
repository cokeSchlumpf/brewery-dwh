package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.brewery.values.BottleSize;
import systems.sales.values.Order;

@Value
@AllArgsConstructor(staticName = "apply")
public class MakeBeerOrderCommand implements CustomerMessage{

    ActorRef<Done> ack;
    Order order;
}
