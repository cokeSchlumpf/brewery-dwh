package simulation.entities.employee.messages;

import java.util.List;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import systems.sales.values.Product;

@Value
@AllArgsConstructor(staticName = "apply")
public class CheckBeerSupplyResponse{

    List<Product> inventory;
    ActorRef<Done> ack;

}
