package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import simulation.entities.employee.messages.CheckBeerSupplyResponse;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class AskBeerSupplyResponseReceived implements CustomerMessage{
    CheckBeerSupplyResponse response;

}
