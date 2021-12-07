package simulation.clock;


import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class Scheduler<T> {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

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

    public Scheduler<T> run(Consumer<Instant> op) {
        this.actions.registerAction(now -> {
            try {
                op.accept(now);
            } catch (Exception e) {
                LOG.error("An exception occurred in scheduled runnable", e);
            }
        });
        return this;
    }

    public Scheduler<T> run(Runnable op) {
        this.actions.registerAction(dateTime -> op.run());
        return this;
    }

    public Scheduler<T> sendMessage(BiFunction<Instant, ActorRef<Done>, T> messageFactory) {
        this.actions.registerAction(moment -> Clock
            .getInstance()
            .startSingleTimer(
                UUID.randomUUID().toString(),
                Duration.ZERO,
                ctx,
                (ActorRef<Done> ref) -> messageFactory.apply(moment, ref)));

        return this;
    }

    public Scheduler<T> sendMessage(Function<ActorRef<Done>, T> messageFactory) {
        return sendMessage((dateTime, ref) -> messageFactory.apply(ref));
    }

    public void schedule() {
        this.actions.init(Clock.getInstance().getNowAsInstant());
    }

    private interface ActionBlock {

        void init(Instant time);

        void registerAction(Consumer<Instant> action);

        void withNext(ActionBlock actions);

    }

    @AllArgsConstructor(staticName = "apply")
    private static class InitialActions implements ActionBlock {

        private final List<Consumer<Instant>> actions;

        private ActionBlock next;

        public static InitialActions apply() {
            return apply(Lists.newArrayList(), null);
        }

        @Override
        public void init(Instant time) {
            actions.forEach(c -> c.accept(time));

            if (next != null) {
                next.init(time);
            }
        }

        @Override
        public void registerAction(Consumer<Instant> action) {
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

        private final List<Consumer<Instant>> actions;

        private ActionBlock next;

        public static ScheduledActions apply(Duration delay, String key) {
            return apply(delay, key, Lists.newArrayList(), null);
        }

        @Override
        public void init(Instant time) {
            Clock
                .getInstance()
                .waitFor(key, delay)
                .thenAccept(now -> {
                    actions.forEach(c -> c.accept(now.toInstant(ZoneOffset.UTC)));

                    if (next != null) {
                        next.init(now.toInstant(ZoneOffset.UTC));
                    }
                });
        }

        @Override
        public void registerAction(Consumer<Instant> action) {
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
