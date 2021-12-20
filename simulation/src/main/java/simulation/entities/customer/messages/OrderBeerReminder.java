package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This message is sent by the customer to her-/ himself as a reminder to order new beer.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class OrderBeerReminder implements Message {

    ActorRef<Done> ack;

}
