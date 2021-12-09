package simulation.entities.customer.state;

import simulation.entities.customer.messages.AskBeerSupply;
import simulation.entities.customer.messages.AskBeerSupplyResponseReceived;
import simulation.entities.customer.messages.ReceiveBeer;

public interface State {

    State onAskBeerSupply(AskBeerSupply msg);

    State onAskBeerSupplyResponseReceived(AskBeerSupplyResponseReceived msg);

    State onReceiveBeer(ReceiveBeer msg);

}
