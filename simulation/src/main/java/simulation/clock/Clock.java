package simulation.clock;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AskPattern;
import akka.japi.Creator;
import common.Operators;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class Clock {

    private static final Logger LOG = LoggerFactory.getLogger(Clock.class);

    /**
     * A default formatter to print date and time.
     */
    public static DateTimeFormatter DEFAULT_FORMATTER =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    private static final AtomicReference<Optional<Clock>> instance = new AtomicReference<>(Optional.empty());

    private final AtomicReference<List<ScheduledEvent>> scheduledEvents;

    private LocalDateTime now;

    private static Clock apply() {
        return apply(new AtomicReference<>(new ArrayList<>()), LocalDateTime.now());
    }

    public static Clock getInstance() {
        return instance.updateAndGet(opt -> {
            if (opt.isEmpty()) {
                return Optional.of(Clock.apply());
            } else {
                return opt;
            }
        }).orElseThrow();
    }

    public static <T> Scheduler<T> scheduler(ActorContext<T> ctx) {
        return Scheduler.apply(ctx);
    }

    public LocalDateTime getNow() {
        return now;
    }

    public Instant getNowAsInstant() {
        return now.toInstant(ZoneOffset.UTC);
    }

    public KillSwitch run() {
        var kill = KillSwitch.apply();

        CompletableFuture.runAsync(() -> {
            while (!kill.isKilled() && !scheduledEvents.get().isEmpty()) {
                var runEvents = scheduledEvents
                    .get()
                    .stream()
                    .filter(e -> e.shouldRun(now))
                    .collect(Collectors.toList());

                if (!runEvents.isEmpty()) {
                    var executed = Operators
                        .allOf(runEvents.stream().map(e -> {
                            LOG.debug("Running event {} at {}", e.getKey(), now);
                            return e.run(now).thenApply(d -> {
                                LOG.debug("Completed event {}", e.getKey());
                                return d;
                            });
                        }))
                        .thenApply(d -> {
                            LOG.debug("Completed all events at moment {}", now);
                            return d;
                        });

                    Operators.ignoreExceptions(() -> executed.toCompletableFuture().get(), LOG);

                    scheduledEvents.getAndUpdate(ev -> {
                        var updated = ev
                            .stream()
                            .filter(event -> event.isActive(now))
                            .collect(Collectors.toList());

                        LOG.debug("Registered event count after cleanup at {}: {}", now, updated.size());

                        return updated;
                    });
                }

                now = now.plus(Duration.ofMinutes(1));
            }

            LOG.info("Clock stopped running (switch: {}, events: {})", kill.isKilled(), scheduledEvents.get().size());
        });

        return kill;
    }

    public void startSingleTimer(String key, Duration delay, Creator<CompletionStage<Done>> operation) {
        scheduledEvents.updateAndGet(events -> {
            events.add(SingleEvent.apply(key, operation, now.plus(delay)));
            return events;
        });
    }

    public void startSingleTimer(String key, Duration delay, Consumer<CompletableFuture<Done>> operation) {
        startSingleTimer(key, delay, () -> {
            var done = new CompletableFuture<Done>();
            operation.accept(done);
            return done;
        });
    }

    public <T> void startSingleTimer(String key, Duration delay, ActorContext<T> ctx, Function<ActorRef<Done>, ?
        extends T> msg) {
        startSingleTimer(
            key, delay,
            done -> AskPattern
                .ask(ctx.getSelf(), msg::apply, Duration.ofSeconds(300), ctx.getSystem().scheduler())
                .thenApply(done::complete));
    }

    public void startSingleTimer(String key, Duration delay, Runnable operation) {
        startSingleTimer(key, delay, done -> {
            operation.run();
            done.complete(Done.getInstance());
        });
    }

    public CompletionStage<LocalDateTime> waitFor(String key, Duration delay) {
        var moment = new CompletableFuture<LocalDateTime>();
        startSingleTimer(key, delay, () -> moment.complete(now));
        return moment;
    }

    public void startPeriodicTimer(String key, Duration interval, Creator<CompletionStage<Done>> operation) {
        scheduledEvents.updateAndGet(events -> {
            events.add(PeriodicEvent.apply(key, operation, interval));
            return events;
        });
    }

    public void startPeriodicTimer(String key, Duration interval, Consumer<CompletableFuture<Done>> operation) {
        startPeriodicTimer(key, interval, () -> {
            var done = new CompletableFuture<Done>();
            operation.accept(done);
            return done;
        });
    }

    public void startPeriodicTimer(String key, Duration interval, Runnable operation) {
        startPeriodicTimer(key, interval, done -> {
            operation.run();
            done.complete(Done.getInstance());
        });
    }

}
