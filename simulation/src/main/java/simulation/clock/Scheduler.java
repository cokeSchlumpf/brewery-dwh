package simulation.clock;


import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class Scheduler<T> {

    private final Clock clock;

    private final ActorContext<T> ctx;

    private final CompletableFuture<LocalDateTime> trigger;

    private CompletionStage<LocalDateTime> expression;

    public static <T> Scheduler<T> apply(Clock clock, ActorContext<T> ctx) {
        var trigger = new CompletableFuture<LocalDateTime>();
        return apply(clock, ctx, trigger, trigger.toCompletableFuture());
    }

    public Scheduler<T> waitFor(Duration delay, String key) {
        expression = expression.thenCompose(i -> clock.waitFor(key, delay));
        return this;
    }

    public Scheduler<T> waitFor(Duration delay) {
        return waitFor(delay, UUID.randomUUID().toString());
    }

    public Scheduler<T> run(Consumer<Instant> op) {
        expression = expression.thenApply(dateTime -> {
            op.accept(dateTime.toInstant(ZoneOffset.UTC));
            return dateTime;
        });

        return this;
    }

    public Scheduler<T> run(Runnable op) {
        expression = expression.thenApply(dateTime -> {
            op.run();
            return dateTime;
        });

        return this;
    }

    public Scheduler<T> sendMessage(BiFunction<Instant, ActorRef<Done>, T> messageFactory) {
        expression = expression.thenApply(moment -> {
            clock
                .startSingleTimer(
                    UUID.randomUUID().toString(),
                    Duration.ZERO,
                    ctx,
                    (ActorRef<Done> ref) -> messageFactory.apply(moment.toInstant(ZoneOffset.UTC), ref));

            return moment;
        });

        return this;
    }

    public Scheduler<T> sendMessage(Function<ActorRef<Done>, T> messageFactory) {
        return sendMessage((dateTime, ref) -> messageFactory.apply(ref));
    }

    public void schedule() {
        trigger.complete(clock.getNow());
    }

}
