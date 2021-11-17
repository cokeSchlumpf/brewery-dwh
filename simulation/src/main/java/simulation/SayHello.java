package simulation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;
import lombok.Value;

public class SayHello extends AbstractBehavior<SayHello.Message> {

    interface Message {}

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class SayHelloToMe implements Message {
        String name;
        ActorRef<? super String> reply;
    }

    public SayHello(ActorContext<SayHello.Message> context) {
        super(context);
    }

    public static Behavior<SayHello.Message> create() {
        return Behaviors.setup(SayHello::new);
    }

    @Override
    public Receive<SayHello.Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(SayHelloToMe.class, msg -> {
                msg.getReply().tell("Hello " + msg.name + "!");
                return Behaviors.stopped();
            })
            .build();
    }

}
