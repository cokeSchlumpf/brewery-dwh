package simulation.entities.customer;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import simulation.clock.Clock;
import simulation.entities.customer.behaviors.CustomerBehavior;
import simulation.entities.customer.messages.Message;
import simulation.entities.customer.messages.OrderBeerReminder;
import simulation.entities.customer.messages.ReceiveBeerOrderConfirmation;
import simulation.entities.customer.messages.SelectBeersToOrder;
import simulation.entities.onlinestore.OnlineStore;
import simulation.entities.onlinestore.messages.BrowseOffers;
import simulation.entities.onlinestore.messages.PlaceOrder;

import java.time.Duration;

public final class Customer extends AbstractBehavior<Message> {

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
            Clock
                .scheduler(ctx)
                .waitFor(behavior.getNextOrderDelay())
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
        var msgAdapter = this
            .getContext()
            .messageAdapter(
                BrowseOffers.BrowseOffersResponse.class,
                resp -> SelectBeersToOrder.apply(resp, msg.getAck()));

        store.tell(BrowseOffers.apply(msgAdapter));
    }

    private void onReceiveBeerOrderConfirmation(ReceiveBeerOrderConfirmation msg) {
        behavior.setCustomerId(msg.getResponse().getCustomerId());

        Clock
            .scheduler(this.getContext())
            .waitFor(behavior.getNextOrderDelay())
            .ask(OrderBeerReminder::apply)
            .scheduleAndAcknowledge(msg.getAck());
    }

    private void onSelectBeersToOrder(SelectBeersToOrder msg) {
        var orderItems = behavior.generateOrder(msg.getOffers().getInventory());
        var customer = behavior.getCustomer();

        if (orderItems.size() > 0) {
            store.tell(PlaceOrder.apply(customer, orderItems, getContext().messageAdapter(
                PlaceOrder.PlaceOrderResponse.class, res -> ReceiveBeerOrderConfirmation.apply(res, msg.getAck()))));
        } else {
            Clock
                .scheduler(this.getContext())
                .waitFor(Duration.ofDays(2))
                .ask(OrderBeerReminder::apply)
                .scheduleAndAcknowledge(msg.getAck());
        }
    }

}
