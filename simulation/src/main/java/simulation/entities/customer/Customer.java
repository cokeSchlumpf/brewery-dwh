package simulation.entities.customer;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.customer.behaviors.CustomerBehavior;
import simulation.entities.customer.messages.*;
import simulation.entities.onlinestore.OnlineStore;
import simulation.entities.onlinestore.messages.BrowseOffers;
import simulation.entities.onlinestore.messages.PlaceOrder;
import systems.sales.values.OrderItem;

import java.time.Duration;
import java.util.List;

public final class Customer extends AbstractBehavior<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(Customer.class);

    private final ActorRef<OnlineStore.Message> store;

    private final CustomerBehavior behavior;

    public Customer(ActorContext<Message> context, ActorRef<OnlineStore.Message> store, CustomerBehavior behavior) {
        super(context);
        this.store = store;
        this.behavior = behavior;
    }

    public static Behavior<Message> create(
        ActorRef<OnlineStore.Message> store,
        CustomerBehavior behavior) {

        return Behaviors.setup(ctx -> {
            var delay = behavior.getNextOrderDelay();
            LOG.info("Creating customer with a delay of " + behavior.getCustomer() + " "+ delay.toMinutes());
            Clock
                .scheduler(ctx)
                .waitFor(Duration.ofDays(4))
                .ask(OrderBeerReminder::apply)
                .schedule();

            return new Customer(ctx, store, behavior);
        });
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(OrderBeerReminder.class, msg -> {
                onOrderBeerReminder(msg);
                return this;
            })
            .onMessage(ReceiveBeerOrderConfirmation.class, msg -> {
                onReceiveBeerOrderConfirmation(msg);
                return this;
            })
            .onMessage(SelectBeersToOrder.class, msg -> {
                onSelectBeersToOrder(msg);
                return this;
            })
            .build();
    }

    private void onOrderBeerReminder(OrderBeerReminder msg) {
        log("Beer order reminder " + behavior.getCustomer());
        var msgAdapter = this
            .getContext()
            .messageAdapter(
                BrowseOffers.BrowseOffersResponse.class,
                resp -> SelectBeersToOrder.apply(resp, msg.getAck()));

        store.tell(BrowseOffers.apply(msgAdapter));
    }

    private void onReceiveBeerOrderConfirmation(ReceiveBeerOrderConfirmation msg) {
        log("Received beer order confirmation.");
        behavior.setCustomerId(msg.getResponse().getCustomerId());
        // hier taste evaluation?




        var delay = Duration.ofDays(4);
        Clock
            .scheduler(this.getContext())
            .waitFor(delay)
            .ask(OrderBeerReminder::apply)
            .scheduleAndAcknowledge(msg.getAck());
    }

    private void onSelectBeersToOrder(SelectBeersToOrder msg) {
        var orderItems = behavior.generateOrder(msg.getOffers().getInventory());
        var customer = behavior.getCustomer();

        if (orderItems.size() > 0) {
            log("Selecting Beers...");
            store.tell(PlaceOrder.apply(customer, orderItems, getContext().messageAdapter(
                PlaceOrder.PlaceOrderResponse.class, res -> ReceiveBeerOrderConfirmation.apply(res, msg.getAck()))));
        } else {
            log("No available beers, will check again in 2 days.");
            Clock
                .scheduler(this.getContext())
                .waitFor(Duration.ofDays(2))
                .ask(OrderBeerReminder::apply)
                .scheduleAndAcknowledge(msg.getAck());
        }
    }

    private void log(String message, Object... args) {
        LOG
            .info(String.format("%s -- %s -- %s", Clock.getInstance()
            .getNow(), behavior.getCustomer().toString(), String.format(message, args)));
    }

}
