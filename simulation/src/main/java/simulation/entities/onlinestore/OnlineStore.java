package simulation.entities.onlinestore;

import akka.Done;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.onlinestore.messages.*;
import systems.sales.SalesManagementSystem;
import systems.sales.values.Customer;

import java.util.stream.Collectors;

public final class OnlineStore extends AbstractBehavior<OnlineStore.Message> {

    public interface Message {
    }

    private static final Logger LOG = LoggerFactory.getLogger(OnlineStore.class);

    private final SalesManagementSystem salesManagementSystem;

    public OnlineStore(ActorContext<Message> actor, SalesManagementSystem salesManagementSystem) {
        super(actor);
        this.salesManagementSystem = salesManagementSystem;
    }

    public static Behavior<Message> create(SalesManagementSystem sms) {
        return Behaviors.setup(actor -> new OnlineStore(actor, sms));
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(BrowseOffers.class, msg -> {
                this.onBrowseOffers(msg);
                return Behaviors.same();
            })
            .onMessage(CheckOpenOrders.class, msg -> {
                this.onCheckOpenOrders(msg);
                return Behaviors.same();
            })
            .onMessage(MarkOrderAsShipped.class, msg -> {
                this.onMarkOrderAsShipped(msg);
                return Behaviors.same();
            })
            .onMessage(PlaceOrder.class, msg -> {
                this.onPlaceOrder(msg);
                return Behaviors.same();
            })
            .build();

    }

    private void onBrowseOffers(BrowseOffers msg) {
        this.log("Received a get request");

        var inventory = this
            .salesManagementSystem
            .getStockProducts()
            .listAvailableProducts()
            .stream()
            .filter(p -> p.getAmountAvailable() > 0)
            .collect(Collectors.toList());

        msg.getResponse().tell(BrowseOffers.BrowseOffersResponse.apply(inventory));
    }

    private void onCheckOpenOrders(CheckOpenOrders msg) {
        log("Checking Open Orders - View currents stock:\n", stockToString());
        msg.getOrders().tell(CheckOpenOrders.CheckOrdersResponse.apply(this.salesManagementSystem.getOrders().getAllOrders()));
    }

    private void onMarkOrderAsShipped(MarkOrderAsShipped msg) {
        var order = this.salesManagementSystem
            .getOrders()
            .getOrderById(msg.getOrderId())
            .withDelivered(Clock.getInstance().getNowAsInstant());

        order
            .getItems()
            .forEach(item -> salesManagementSystem.getStockProducts().removeFromStock(item.getBeer(), item.getBottles()));

        this.salesManagementSystem.getOrders().updateOrder(order);
        msg.getAck().tell(Done.getInstance());
    }

    private void onPlaceOrder(PlaceOrder msg) {
        var customer = createOrGetCustomer(msg.getCustomer());
        var orderId = this.salesManagementSystem.getOrders().insertOrder(customer.getId(), Clock.getInstance().getNowAsInstant(), msg.getItems());
        var response = PlaceOrder.PlaceOrderResponse.apply(customer.getId(), orderId);

        msg.getConfirmTo().tell(response);
    }

    private Customer createOrGetCustomer(PlaceOrder.Customer customer) {
        if (customer instanceof PlaceOrder.NewCustomer) {
            var c = (PlaceOrder.NewCustomer) customer;

            return this
                .salesManagementSystem
                .getCustomers()
                .insertCustomer(c.getEmail(), c.getFirstname(), c.getName(), c.getAddress());
        } else if (customer instanceof PlaceOrder.RegisteredCustomer) {
            return this
                .salesManagementSystem
                .getCustomers()
                .getCustomerById(((PlaceOrder.RegisteredCustomer) customer).getId());
        } else {
            throw new RuntimeException("Unknown instance of customer");
        }
    }

    public void log(String message, Object...args) {
        LOG.info(String.format("%s -- %s", Clock.getInstance().getNow(), String.format(message, args)));
    }


    private String stockToString() {
        var available = salesManagementSystem.getStockProducts().listAvailableProducts();
        var result = new StringBuilder();

        available.forEach(prod -> result
            .append("> ")
            .append(prod.getProduct().getProductName())
            .append(", amount:")
            .append(prod.getAmount())
            .append(", reserved: ")
            .append(prod.getReserved())
            .append("\n"));

        return result.toString();
    }
}
