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
import systems.sales.values.Product;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
        ctx.log("Received response from brewery: " + on_sale.toString());

        List<Product> selected_products;
        List<OrderItem> orderItems = Lists.<OrderItem>newArrayList();
        Order order = null;

        switch(ctx.getCustomerType()){
            case NORMAL:
                // normal customer only orders beer, if his favorite beer is on stock
                selected_products = on_sale
                                        .stream()
                                        .filter(product -> ctx.getFavoriteBeers().contains(product.getProductName()))
                                        .limit(3)
                                        .collect(Collectors.toList());
                if(!selected_products.isEmpty()){
                    for (Product product: selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product,Math.min(P.randomInteger(6, 5),product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }
                break;
                
            case STUDENT:
                // student orders a lot of cheap beer
                selected_products = on_sale
                        .stream()
                        .filter(product -> (ctx.getFavoriteBeers().contains(product.getProductName()) && product.getPrice()<1.0) || product.getPrice()<0.7)
                        .limit(3)
                        .collect(Collectors.toList());
                if(!selected_products.isEmpty()){
                    for (Product product: selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product,Math.min(P.randomInteger(18, 6.0),product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }
                break;

            default:
                selected_products = P.nRandomItems(on_sale, P.randomInteger(5,3.0));
                if(!selected_products.isEmpty()){
                    for (Product product: selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product,Math.min(P.randomInteger(6, 5),product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }
                break;
        }

        /*Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofMinutes(20)))
            .ask(ctx.getEmployee(), ack -> BeerOrderCommand.apply(ack, order,ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)))
            .scheduleAndAcknowledge(msg.getResponse().getAck());
        //.tell(Done.getInstance());*/

        ctx.getEmployee().tell(BeerOrderCommand.apply(msg.getResponse().getAck(), order, ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)));

        return this;
    }

    @Override
    public State onReceiveBeer(ReceiveBeer msg) {
        ctx.log("Received beer!");
        msg.getResponse().getAck().tell(Done.getInstance());
        return this;
    }

}
