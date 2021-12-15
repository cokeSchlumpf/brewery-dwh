package simulation.entities.onlinestore.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import systems.sales.values.Product;

import java.util.List;

public class CheckBeerStoreResponse implements OnlineStoreMessage{
    List<Product> inventory;
    ActorRef<Done> ack;
}
