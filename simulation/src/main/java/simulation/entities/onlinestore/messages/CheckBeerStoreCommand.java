package simulation.entities.onlinestore.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "apply")
public class CheckBeerStoreCommand implements OnlineStoreMessage{
    ActorRef<Done> ack;
    CheckBeerStoreResponse response;
}
