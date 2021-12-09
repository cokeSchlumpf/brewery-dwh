package simulation.entities.customer.messages;

import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.messages.SendBeerCommand;


@Value
@AllArgsConstructor(staticName = "apply")
public class ReceiveBeer implements CustomerMessage{
    SendBeerCommand response;
}
