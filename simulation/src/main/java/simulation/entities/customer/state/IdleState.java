package simulation.entities.customer.state;

import akka.Done;
import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.customer.CustomerContext;
import simulation.entities.customer.messages.AskBeerSupply;
import simulation.entities.customer.messages.AskBeerSupplyResponseReceived;
import simulation.entities.customer.messages.ReceiveBeer;
import simulation.entities.employee.messages.BeerOrderCommand;
import simulation.entities.employee.messages.CheckBeerSupply;
import simulation.entities.employee.messages.CheckBeerSupplyResponse;
import simulation.entities.employee.messages.SendBeerCommand;
import systems.sales.values.Order;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;


@AllArgsConstructor(staticName = "apply")
public class IdleState implements State {

    private final CustomerContext ctx;

    @Override
    public State onAskBeerSupply(AskBeerSupply msg) {
        ctx.log("Asks Brewery for beer supply");
        ctx.getEmployee().tell(CheckBeerSupply.apply(msg.getAck(),ctx.getActor().messageAdapter(CheckBeerSupplyResponse.class, AskBeerSupplyResponseReceived::apply)));
        return this;
    }

    @Override
    public State onAskBeerSupplyResponseReceived(AskBeerSupplyResponseReceived msg) {

        ctx.log("Received response from brewery: ");

        var on_sale = msg.getResponse().getInventory();

        // Places an order (different customer types)
        switch(ctx.getCustomerType()){
            case NORMAL:
                ctx.log("Normal order");
                var item = P.randomItem(on_sale);
                Order order = Order.predefinedOrder();
                ctx.getEmployee().tell(BeerOrderCommand.apply(msg.getResponse().getAck(), order, ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)));
                break;
        }
        return this;
    }

    @Override
    public State onReceiveBeer(ReceiveBeer msg) {
        ctx.log("Received beer!");
        msg.getResponse().getAck().tell(Done.getInstance());
        return this;
    }

}
