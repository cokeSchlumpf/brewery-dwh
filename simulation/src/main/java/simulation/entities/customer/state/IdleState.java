package simulation.entities.customer.state;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AskPattern;
import com.google.common.collect.Lists;
import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.customer.CustomerContext;
import simulation.entities.customer.messages.*;
import simulation.entities.employee.messages.*;
import simulation.entities.onlinestore.messages.GetBeerStoreRequest;
import simulation.entities.onlinestore.messages.GetBeerStoreResponse;
import simulation.entities.onlinestore.messages.PutOrderRequest;
import systems.sales.values.Order;
import systems.sales.values.OrderItem;
import systems.sales.values.Product;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor(staticName = "apply")
public class IdleState implements State {

    private final CustomerContext ctx;

    @Override
    public State onAskBeerSupply(AskBeerSupply msg) {
        ctx.log("Asks Brewery for beer supply");

        // Asks brewery employee directly
        // ctx.getEmployee().tell(CheckBeerSupply.apply(msg.getAck(), ctx.getActor().messageAdapter(CheckBeerSupplyResponse.class, AskBeerSupplyResponseReceived::apply)));

        // make request via online store
        ctx.getOnlinestore().tell(GetBeerStoreRequest.apply(msg.getAck(), ctx.getActor().messageAdapter(GetBeerStoreResponse.class, AskBeerSupplyOnlineResponseReceived::apply)));
        return this;
    }

    @Override
    public State onAskBeerSupplyResponseReceived(AskBeerSupplyResponseReceived msg) {

        var on_sale = msg.getResponse().getInventory();
        ctx.log("Received response from brewery: " + on_sale.toString());

        //ToDo: Bug fixing Uhr
        Order order = generateOrder(on_sale);
        var adapter = ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply);
        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofMinutes(20)),"Wait")
            .run(()->{ctx.log("Checkpoint !");})
            .ask(ctx.getEmployee(), ack -> BeerOrderCommand.apply(ack, order,adapter))
            .run(()-> ctx.log("Checkpoint 2"))
                //.schedule();
            .scheduleAndAcknowledge(msg.getResponse().getAck());
        //msg.getResponse().getAck().tell(Done.getInstance());

       // ctx.getEmployee().tell(BeerOrderCommand.apply(msg.getResponse().getAck(), order, ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)));
        return this;
    }

    @Override
    public State onReceiveBeer(ReceiveBeer msg) {
        ctx.log("Received beer!");
        msg.getResponse().getAck().tell(Done.getInstance());
        return this;
    }

    @Override
    public State onMakeBeerOrderCommand(MakeBeerOrderCommand msg) {
        // directly calls employee
        ctx.getEmployee().tell(BeerOrderCommand.apply(msg.getAck(),msg.getOrder(),ctx.getActor().messageAdapter(SendBeerCommand.class, ReceiveBeer::apply)));

        // places order in online store
        //ctx.getOnlinestore().tell(PutOrderRequest.apply(msg.getOrder(), ctx.getActor().getSelf(), msg.getAck()));

        return this;
    }

    @Override
    public State onAskBeerSupplyOnlineResponseReceived(AskBeerSupplyOnlineResponseReceived msg) {

        ctx.log("Online store has the following products on stock "+ msg.getResponse().getInventory().toString());

        var makeOrder = true;

        if(makeOrder){
            Order order = generateOrder(msg.getResponse().getInventory());
            ctx.log(order.getItems().toString());
            Clock
                    .scheduler(ctx.getActor())
                    .waitFor(P.randomDuration(Duration.ofMinutes(10)))
                    .ask((ack) -> MakeBeerOrderCommand.apply(ack, order))
                    .schedule();
            msg.getResponse().getAck().tell(Done.getInstance());
        }
        else{
            ctx.log("does not make an order");
            msg.getResponse().getAck().tell(Done.getInstance());
        }

        return this;
    }

    @Override
    public State onReceiveBeerFromOnlineStore(ReceiveBeerFromOnlineStore msg) {
        ctx.log("Customer received Shipment from online store.");
        msg.getAck().tell(Done.getInstance());
        return this;
    }

    private Order generateOrder(List<Product> onSale){
        List<Product> selected_products;
        List<OrderItem> orderItems = Lists.<OrderItem>newArrayList();
        Order order = null;

        switch(ctx.getCustomerType()) {
            case NORMAL:
                // normal customer only orders beer, if his favorite beer is on stock
                selected_products = onSale
                        .stream()
                        .filter(product -> ctx.getFavoriteBeers().contains(product.getProductName()))
                        .limit(3)
                        .collect(Collectors.toList());
                if (!selected_products.isEmpty()) {
                    for (Product product : selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product, Math.min(P.randomInteger(12, 3.0), product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }
                break;

            case STUDENT:
                // student orders a lot of cheap beer
                selected_products = onSale
                        .stream()
                        .filter(product -> (ctx.getFavoriteBeers().contains(product.getProductName()) && product.getPrice() < 1.0) || product.getPrice() < 0.7)
                        .limit(3)
                        .collect(Collectors.toList());
                if (!selected_products.isEmpty()) {
                    for (Product product : selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product, Math.min(P.randomInteger(18, 6.0), product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }
                break;

            case GOURMET:
                // gourmet orders random but little number of beers
                selected_products = P.nRandomItems(onSale, P.randomInteger(5, 3.0));
                if (!selected_products.isEmpty()) {
                    for (Product product : selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product, Math.min(P.randomInteger(6, 1.0), product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }

            case PARTY_PLANNER:
                // party planner orders a lot
                selected_products = P.nRandomItems(onSale, 2);
                if (!selected_products.isEmpty()) {
                    for (Product product : selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product, Math.min(P.randomInteger(50, 20.0), product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }

            default:
                selected_products = P.nRandomItems(onSale, P.randomInteger(5, 3.0));
                if (!selected_products.isEmpty()) {
                    for (Product product : selected_products
                    ) {
                        orderItems.add(OrderItem.apply(product, Math.min(P.randomInteger(6, 5), product.getInventory())));
                    }
                    order = Order.apply(ctx.getCustomer(), Clock.getInstance().getNowAsInstant(), null, orderItems);
                }
                break;
        }

        return order;
    }

}
