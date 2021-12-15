package simulation.entities.onlinestore;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import simulation.entities.employee.messages.EmployeeMessage;
import simulation.entities.onlinestore.state.State;
import simulation.entities.onlinestore.state.IdleState;
import simulation.entities.onlinestore.messages.OnlineStoreMessage;
import systems.sales.SalesManagementSystem;

public class OnlineStore extends AbstractBehavior<OnlineStoreMessage> {

    private State state;

    public OnlineStore(OnlineStoreContext ctx){
        super(ctx.getActor());
        this.state = IdleState.apply(ctx);
    }

    public static Behavior<OnlineStoreMessage> create(SalesManagementSystem sms, ActorRef<EmployeeMessage> employee){
        return Behaviors.setup( actor -> {
            var ctx = OnlineStoreContext.apply(actor, sms, employee, "www.brewery.com");
            return new OnlineStore(ctx);
        });
    }

    @Override
    public Receive<OnlineStoreMessage> createReceive() {
        return null;
    }
}
