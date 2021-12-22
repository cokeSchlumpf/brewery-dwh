package simulation.entities.employee;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import simulation.clock.Clock;
import simulation.entities.brewery.Brewery;
import simulation.entities.employee.brewing.BrewingEmployee;
import simulation.entities.employee.brewing.messages.BrewABeer;
import simulation.entities.employee.delivery.DeliveringEmployee;
import simulation.entities.employee.delivery.messages.CheckOrders;
import simulation.entities.employee.messages.BrewABeerReminder;
import simulation.entities.employee.messages.CheckOrdersReminder;
import simulation.entities.onlinestore.OnlineStore;
import systems.brewery.BreweryManagementSystem;
import systems.sales.SalesManagementSystem;

import java.time.Duration;

public final class Employee extends AbstractBehavior<Employee.Message> {

    public interface Message {}

    private final ActorRef<BrewingEmployee.Message> brewing;

    private final ActorRef<DeliveringEmployee.Message> delivering;

    public Employee(ActorContext<Employee.Message> actor, ActorRef<BrewingEmployee.Message> brewing, ActorRef<DeliveringEmployee.Message> delivering) {
        super(actor);
        this.brewing = brewing;
        this.delivering = delivering;
    }

    public static Behavior<Employee.Message> create(systems.reference.model.Employee employee, ActorRef<OnlineStore.Message> store, BreweryManagementSystem bms, SalesManagementSystem sms, Brewery brewery) {
        return Behaviors.setup(ctx -> {
            var brewing = ctx.spawn(BrewingEmployee.create(employee, bms, sms, brewery), "brewing");
            var delivering = ctx.spawn(DeliveringEmployee.create(employee, store), "delivering");

            /*
            Clock
                .getInstance()
                .startPeriodicTimer("brew-a-beer", Duration.ofDays(14), () -> AskPattern.ask(
                    ctx.getSelf(),
                    ack -> BrewABeerReminder.apply("foo", ack),
                    Duration.ofMinutes(10),
                    ctx.getSystem().scheduler()));
             */

            Clock
                .getInstance()
                .startPeriodicTimer("check-orders", Duration.ofHours(6), () -> AskPattern.ask(
                    ctx.getSelf(),
                    CheckOrdersReminder::apply,
                    Duration.ofMinutes(10),
                    ctx.getSystem().scheduler()));

            return new Employee(ctx, brewing, delivering);
        });
    }

    @Override
    public Receive<Employee.Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(BrewABeerReminder.class, msg -> {
                this.onBrewABeerReminder(msg);
                return Behaviors.same();
            })
            .onMessage(CheckOrdersReminder.class, msg -> {
                this.onCheckOrdersReminder(msg);
                return Behaviors.same();
            })
            .build();
    }

    private void onBrewABeerReminder(BrewABeerReminder msg) {
        brewing.tell(BrewABeer.apply(msg.getBeerKey(), msg.getAck()));
    }

    private void onCheckOrdersReminder(CheckOrdersReminder msg) {
        delivering.tell(CheckOrders.apply(msg.getAck()));
    }

}
