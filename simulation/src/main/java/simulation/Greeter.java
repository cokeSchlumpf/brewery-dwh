package simulation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;
import lombok.Value;

public class Greeter extends AbstractBehavior<Greeter.Message> {

    public interface Message {}

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class MachWas implements Message {

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class HandleResponse implements Message {

        String response;

    }

    public Greeter(ActorContext<Message> context) {
        super(context);
    }

    public static Behavior<Message> createGreeter() {
        return Behaviors.setup(ctx -> {
            ctx.getSelf().tell(MachWas.apply());
            return new Greeter(ctx);
        });
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(HandleResponse.class, resp -> {
                System.out.println(resp.getResponse());
                return Behaviors.same();
            })
            .onMessage(MachWas.class, str -> {
                ActorRef<SayHello.Message> ref = this.getContext().spawn(SayHello.create(), "say-hello");
                ref.tell(SayHello.SayHelloToMe.apply("kunibert", getContext().messageAdapter(String.class, HandleResponse::apply)));

                return Behaviors.same();
            })
            .build();
    }

}
