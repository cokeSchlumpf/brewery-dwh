package simulation.clock;


import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AskPattern;
import com.google.common.collect.Lists;
import common.Operators;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class Scheduler<T> {

    private final ActorContext<T> ctx;

    private final ActionBlock actions;

    public static <T> Scheduler<T> apply(ActorContext<T> ctx) {
        return apply(ctx, InitialActions.apply());
    }

    public Scheduler<T> waitFor(Duration delay, String key) {
        this.actions.withNext(ScheduledActions.apply(delay, key));
        return this;
    }

    public Scheduler<T> waitFor(Duration delay) {
        return waitFor(delay, UUID.randomUUID().toString());
    }

    public Scheduler<T> run(BiConsumer<Instant, CompletableFuture<Done>> op) {
        this.actions.registerAction(moment -> op.accept(moment.getNow(), moment.getAck()));
        return this;
    }

    public Scheduler<T> run(Consumer<Instant> op) {
        return this.run((now, ack) -> {
            op.accept(now);
            ack.complete(Done.getInstance());
        });
    }

    public Scheduler<T> run(Runnable op) {
        return this.run(now -> op.run());
    }

    public <R> Scheduler<T> ask(ActorRef<R> recipient, BiFunction<Instant, ActorRef<Done>, R> messageFactory) {
        this.run((now, ack) -> {
                var a = AskPattern
            .ask(recipient, (ActorRef<Done> ref) -> {
                var b = messageFactory.apply(now, ref);
                return b;
                }, Duration.ofMinutes(1),
                ctx.getSystem()
                    .scheduler());
                a.thenAccept(done -> {
                ack.complete(done);});
        });

        return this;
    }

    public Scheduler<T> ask(BiFunction<Instant, ActorRef<Done>, T> messageFactory) {
        return this.ask(ctx.getSelf(), messageFactory);
    }

    public Scheduler<T> ask(Function<ActorRef<Done>, T> messageFactory) {
        return ask((dateTime, ref) -> messageFactory.apply(ref));
    }

    public <R> Scheduler<T> ask(ActorRef<R> recipient, Function<ActorRef<Done>, R> messageFactory) {
        return ask(recipient, (dateTime, ref) -> messageFactory.apply(ref));
    }

    public void scheduleAndAcknowledge(ActorRef<Done> ack) {
        schedule().thenAccept(ack::tell);
    }

    public CompletionStage<Done> schedule() {
        return this.actions.run(Clock.getInstance().getNowAsInstant());
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ClockMoment {

        Instant now;

        CompletableFuture<Done> ack;

    }

    private interface ActionBlock {

        CompletionStage<Done> run(Instant time);

        void registerAction(Consumer<ClockMoment> action);

        void withNext(ActionBlock actions);

    }

    @AllArgsConstructor(staticName = "apply")
    private static class InitialActions implements ActionBlock {

        private final List<Consumer<ClockMoment>> actions;

        private ActionBlock next;

        public static InitialActions apply() {
            return apply(Lists.newArrayList(), null);
        }

        @Override
        public CompletionStage<Done> run(Instant time) {
            var result = Operators.allOf(actions
                    .stream()
                    .map(action -> {
                        var ack = new CompletableFuture<Done>();
                        action.accept(ClockMoment.apply(time, ack));
                        return ack;
                    }))
                .thenApply(list -> Done.getInstance());

            result = result.thenCompose(done -> {
                if (next != null) {
                    return next.run(time);
                } else {
                    return CompletableFuture.completedFuture(done);
                }
            });

            return result;
        }

        @Override
        public void registerAction(Consumer<ClockMoment> action) {
            if (next != null) {
                next.registerAction(action);
            } else {
                this.actions.add(action);
            }
        }

        @Override
        public void withNext(ActionBlock actions) {
            this.next = actions;
        }

    }

    @AllArgsConstructor(staticName = "apply")
    private static class ScheduledActions implements ActionBlock {

        private final Duration delay;

        private final String key;

        private final List<Consumer<ClockMoment>> actions;

        private ActionBlock next;

        public static ScheduledActions apply(Duration delay, String key) {
            return apply(delay, key, Lists.newArrayList(), null);
        }

        @Override
        public CompletionStage<Done> run(Instant time) {
            Clock
                .getInstance()
                .startSingleTimer(key, delay, () -> {
                    var now = Clock.getInstance().getNowAsInstant();

                    return Operators
                        .allOf(actions
                            .stream()
                            .map(action -> {
                                var ack = new CompletableFuture<Done>();
                                var moment = ClockMoment.apply(now, ack);
                                action.accept(moment);
                                return ack;
                            }))
                        .thenApply(ignore -> {
                            if (next != null) {
                                next.run(now);
                            }

                            return Done.getInstance();
                        });
                });


            var result = CompletableFuture.completedFuture(Done.getInstance());

            result = result.thenCompose(done -> {
                if (next != null) {
                    return next.run(time);
                } else {
                    return CompletableFuture.completedFuture(done);
                }
            });

            return result;
        }

        @Override
        public void registerAction(Consumer<ClockMoment> action) {
            if (next != null) {
                next.registerAction(action);
            } else {
                this.actions.add(action);
            }
        }

        @Override
        public void withNext(ActionBlock actions) {
            this.next = actions;
        }

    }

}
