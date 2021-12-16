package simulation.entities.customer.state;

import simulation.entities.customer.messages.*;

public interface State {

    State onAskBeerSupply(AskBeerSupply msg);

    State onAskBeerSupplyResponseReceived(AskBeerSupplyResponseReceived msg);

    State onReceiveBeer(ReceiveBeer msg);

    State onMakeBeerOrderCommand(MakeBeerOrderCommand msg);

    State onAskBeerSupplyOnlineResponseReceived(AskBeerSupplyOnlineResponseReceived msg);

    State onReceiveBeerFromOnlineStore(ReceiveBeerFromOnlineStore msg);

}
