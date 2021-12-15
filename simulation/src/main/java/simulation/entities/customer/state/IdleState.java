package simulation.entities.customer.state;

import akka.Done;
import com.google.common.collect.Lists;
import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.customer.CustomerContext;
import simulation.entities.customer.messages.AskBeerSupply;
import simulation.entities.customer.messages.AskBeerSupplyResponseReceived;
import simulation.entities.customer.messages.ReceiveBeer;
import simulation.entities.employee.messages.*;
import systems.sales.values.Order;
import systems.sales.values.OrderItem;

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

        var on_sale = msg.getResponse().getInventory();

        // Places an order (different customer types)
        switch(ctx.getCustomerType()){
            case NORMAL:

                ctx.log("Received response from brewery: ");
                ctx.log(on_sale.toString());
                var item = P.randomItem(on_sale);
                var orderItem = OrderItem.apply(item, (int) P.randomDouble(item.getInventory()/2, item.getInventory()*0.1));
                List<OrderItem> items = Lists.<OrderItem>newArrayList();
                items.add(orderItem);
                Order order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, items);


                /*Clock
                    .scheduler(ctx.getActor())
                    .waitFor(P.randomDuration(Duration.ofMinutes(20)))
                    .sendMessage(ctx.getEmployee(), ack -> BeerOrderCommand.apply(ack, order,ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)))
                    .schedule();

                msg.getResponse().getAck().tell(Done.getInstance());*/
                //ctx.getEmployee().tell(BeerOrderCommand.apply(msg.getResponse().getAck(), order, ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)));

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
