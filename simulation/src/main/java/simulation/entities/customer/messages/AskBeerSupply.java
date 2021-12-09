package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class AskBeerSupply implements  CustomerMessage{
    ActorRef<Done> ack;
}
