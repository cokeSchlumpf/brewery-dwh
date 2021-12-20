package simulation.entities.onlinestore.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.OnlineStore;

/**
 * When an employee has shipped the order, it can be marked as shipped in the system.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class MarkOrderAsShipped implements OnlineStore.Message {

    int orderId;

    ActorRef<Done> ack;

}
