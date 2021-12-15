package simulation.entities.customer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import simulation.clock.Clock;
import simulation.entities.customer.messages.AskBeerSupply;
import simulation.entities.customer.messages.AskBeerSupplyResponseReceived;
import simulation.entities.customer.messages.CustomerMessage;
import simulation.entities.customer.messages.ReceiveBeer;
import simulation.entities.customer.state.IdleState;
import simulation.entities.customer.state.State;
import simulation.entities.customer.values.CustomerType;
import simulation.entities.employee.messages.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;


public class Customer extends AbstractBehavior<CustomerMessage> {

    private State state;

    public Customer(CustomerContext ctx) {
        super(ctx.getActor());
        this.state = IdleState.apply(ctx);
    }

    public static Behavior<CustomerMessage> create(ActorRef<EmployeeMessage> employee) {
        return Behaviors.setup(actor -> {

            /*Clock
                .getInstance()
                .startPeriodicTimer("Customer lifecycle", Duration.ofDays(7), done -> AskPattern
                        .ask(actor.getSelf(), AskBeerSupply::apply, Duration.ofMinutes(10), actor.getSystem().scheduler())
                        .thenApply(reply -> done.complete(reply))
                );*/

            // ToDo: Random favorite beers, basierend auf products
            var favoriteBeers = Arrays.asList("New Year's Bar Beer", "Christmas Foo Beer");
            var ctx = CustomerContext.apply(actor, employee,favoriteBeers, CustomerType.NORMAL, "sam", null);
            return new Customer(ctx);
        });
    }

    @Override
    public Receive<CustomerMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(AskBeerSupply.class, cmd -> {
                    this.state = state.onAskBeerSupply(cmd);
                    return Behaviors.same();
                })
                .onMessage(AskBeerSupplyResponseReceived.class, cmd -> {
                    this.state = state.onAskBeerSupplyResponseReceived(cmd);
                    return Behaviors.same();
                })
                .onMessage(ReceiveBeer.class, cmd -> {
                    this.state = state.onReceiveBeer(cmd);
                    return Behaviors.same();
                })
                .build();
    }
}
