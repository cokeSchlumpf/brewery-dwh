package simulation.entities.onlinestore.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class GetBeerStoreRequest implements OnlineStoreMessage{
    ActorRef<Done> ack;
    ActorRef<GetBeerStoreResponse> response;
}
