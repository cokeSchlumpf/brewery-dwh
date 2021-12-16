package simulation.entities.customer.messages;

import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.messages.GetBeerStoreResponse;

@Value
@AllArgsConstructor(staticName = "apply")
public class AskBeerSupplyOnlineResponseReceived implements CustomerMessage{
    GetBeerStoreResponse response;
}
