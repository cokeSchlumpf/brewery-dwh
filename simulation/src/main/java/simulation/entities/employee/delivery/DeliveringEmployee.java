package simulation.entities.employee.delivery;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import simulation.clock.Clock;
import simulation.entities.employee.delivery.messages.CheckOrders;
import simulation.entities.employee.delivery.messages.MarkOrderShippedConfirmation;
import simulation.entities.employee.delivery.messages.ReviewOpenOrders;
import simulation.entities.onlinestore.OnlineStore;
import simulation.entities.onlinestore.messages.CheckOpenOrders;
import simulation.entities.onlinestore.messages.MarkOrderAsShipped;
import systems.reference.model.Employee;

public final class DeliveringEmployee extends AbstractBehavior<DeliveringEmployee.Message> {

    public interface Message {
    }

    private final Employee employee;

    private final ActorRef<OnlineStore.Message> store;

    private boolean checkingOrders = false;

    private int awaitShippedConfirmations = 0;

    public DeliveringEmployee(ActorContext<Message> context, Employee employee, ActorRef<OnlineStore.Message> store) {
        super(context);
        this.employee = employee;
        this.store = store;
    }

    public static Behavior<Message> create(Employee employee, ActorRef<OnlineStore.Message> store) {
        return Behaviors.setup(ctx -> new DeliveringEmployee(ctx, employee, store));
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(CheckOrders.class, cmd -> {
                onCheckOrders(cmd);
                return this;
            })
            .onMessage(ReviewOpenOrders.class, cmd -> {
                onReviewOpenOrders(cmd);
                return this;
            })
            .onMessage(MarkOrderShippedConfirmation.class, cmd -> {
                onMarkOrderShippedConfirmation(cmd);
                return this;
            })
            .build();
    }

    private void onCheckOrders(CheckOrders cmd) {
        if (checkingOrders) {
            cmd.getAck().tell(Done.getInstance());
        } else {
            var adapter = getContext().messageAdapter(
                CheckOpenOrders.CheckOrdersResponse.class,
                resp -> ReviewOpenOrders.apply(resp.getOpenOrders(), cmd.getAck()));

            store.tell(CheckOpenOrders.apply(adapter));
            checkingOrders = true;
        }
    }

    private void onReviewOpenOrders(ReviewOpenOrders msg) {
        if (msg.getOpenOrders().isEmpty()) {
            msg.getAck().tell(Done.getInstance());
            checkingOrders = false;
        } else {
            msg.getOpenOrders().forEach(order -> {
                store.tell(MarkOrderAsShipped.apply(order.getOrderId(), getContext().messageAdapter(Done.class, done -> MarkOrderShippedConfirmation.apply(msg.getAck()))));
                log("Delivering order `%s` to `%s`", order.getOrderId(), order.getCustomer().getFirstname());
                this.awaitShippedConfirmations += 1;
            });
        }
    }

    private void onMarkOrderShippedConfirmation(MarkOrderShippedConfirmation msg) {
        this.awaitShippedConfirmations -= 1;

        if (this.awaitShippedConfirmations == 0) {
            msg.getAckCheckOrders().tell(Done.getInstance());
            checkingOrders = false;
            log("Finished checking orders");
        }
    }

    private void log(String message, Object... args) {
        getContext().getLog()
            .info(String.format("%s -- %s -- %s", Clock.getInstance()
                .getNow(), employee.getId(), String.format(message, args)));
    }

}
