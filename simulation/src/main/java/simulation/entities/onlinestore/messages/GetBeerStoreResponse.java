package simulation.entities.onlinestore.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import systems.sales.values.Product;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class GetBeerStoreResponse {
    List<Product> inventory;
    ActorRef<Done> ack;
}
