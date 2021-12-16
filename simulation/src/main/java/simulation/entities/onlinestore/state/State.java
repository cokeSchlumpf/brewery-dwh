package simulation.entities.onlinestore.state;

import simulation.entities.onlinestore.messages.GetBeerStoreRequest;
import simulation.entities.onlinestore.messages.PutOrderRequest;

public interface State {

    State onGetBeerStoreRequest(GetBeerStoreRequest cmd);

    State onPutOrderRequest(PutOrderRequest cmd);
}
