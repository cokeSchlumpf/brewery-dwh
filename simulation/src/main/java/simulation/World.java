package simulation;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.pattern.StatusReply;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.employee.Employee;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.EmployeeMessage;

import java.time.Duration;

public final class World extends AbstractBehavior<World.WorldMessage> {

    interface WorldMessage {
    }

    @AllArgsConstructor(staticName = "apply")
    public static class HelloWorld implements WorldMessage {

        ActorRef<StatusReply<Done>> done;

    }

    private final ActorContext<WorldMessage> ctx;

    private final ActorRef<EmployeeMessage> johnny;

    private World(ActorContext<WorldMessage> context, ActorRef<EmployeeMessage> johnny) {
        super(context);
        this.ctx = context;
        this.johnny = johnny;
    }

    public static Behavior<WorldMessage> create() {
        return Behaviors.setup(ctx -> {
            Clock
                .getInstance()
                .startSingleTimer("brew a beer", Duration.ofDays(10), done -> AskPattern
                    .ask(ctx.getSelf(), HelloWorld::apply, Duration.ofSeconds(10), ctx.getSystem().scheduler())
                    .thenApply(reply -> done.complete(reply.getValue())));

            Clock.getInstance().run();

            var johnny = ctx.spawn(Employee.create("johnny"), "johnny");

            return new World(ctx, johnny);
        });
    }

    @Override
    public Receive<WorldMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(HelloWorld.class, msg -> {
                var time = Clock.DEFAULT_FORMATTER.format(Clock.getInstance().getNow());

                AskPattern
                    .ask(
                        johnny,
                        (ActorRef<Done> ref) -> BrewABeerCommand.apply("foo", ref),
                        Duration.ofSeconds(10),
                        ctx.getSystem().scheduler())
                    .thenAccept(done -> msg.done.tell(StatusReply.success(done)));

                return Behaviors.same();
            })
            .build();
    }

}
