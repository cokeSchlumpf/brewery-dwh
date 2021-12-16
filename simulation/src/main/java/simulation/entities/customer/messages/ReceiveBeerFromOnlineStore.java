package simulation.entities.customer.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.messages.ShipOnlineStoreBeerCommand;

@Value
@AllArgsConstructor(staticName = "apply")
public class ReceiveBeerFromOnlineStore implements CustomerMessage{
    //ShipOnlineStoreBeerCommand shipment;
    ActorRef<Done> ack;
}
