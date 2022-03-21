package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.messages.BrowseOffers;

@Value
@AllArgsConstructor(staticName = "apply")
public class SelectBeersToOrder implements Message {

    BrowseOffers.BrowseOffersResponse offers;

    ActorRef<Done> ack;

}
