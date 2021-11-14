package simulation;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.pattern.StatusReply;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;

import java.time.Duration;

public final class World extends AbstractBehavior<World.WorldMessage> {

    interface WorldMessage {
    }

    @AllArgsConstructor(staticName = "apply")
    public static class HelloWorld implements WorldMessage {

        ActorRef<StatusReply<Done>> done;

    }

    private World(ActorContext<WorldMessage> context) {
        super(context);
    }

    public static Behavior<WorldMessage> create() {
        return Behaviors.setup(ctx -> {
            Clock.getInstance().startPeriodicTimer(Duration.ofHours(1), done -> AskPattern
                .ask(ctx.getSelf(), HelloWorld::apply, Duration.ofSeconds(10), ctx.getSystem().scheduler())
                .thenApply(reply -> done.complete(reply.getValue())));

            Clock.getInstance().run();

            return new World(ctx);
        });
    }

    @Override
    public Receive<WorldMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(HelloWorld.class, msg -> {
                var time = Clock.DEFAULT_FORMATTER.format(Clock.getInstance().getNow());

                System.out.println("Hello World! " + time);
                Thread.sleep(1000);
                msg.done.tell(StatusReply.success(Done.getInstance()));
                return this;
            })
            .build();
    }

}
