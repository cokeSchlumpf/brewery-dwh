package simulation.entities.employee.delivery.messages;

import akka.Done;
import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.delivery.DeliveringEmployee;

@Value
@AllArgsConstructor(staticName = "apply")
public class MarkOrderShippedConfirmation implements DeliveringEmployee.Message {

    ActorRef<Done> ackCheckOrders;

}
