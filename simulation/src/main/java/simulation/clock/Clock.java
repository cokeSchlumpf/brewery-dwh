package simulation.clock;

import akka.Done;
import akka.japi.Creator;
import lombok.AllArgsConstructor;
import simulation.Operators;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class Clock {

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

    public LocalDateTime getNow() {
        return now;
    }

    public KillSwitch run() {
        var kill = KillSwitch.apply();

        CompletableFuture.runAsync(() -> {
            while (!kill.isKilled()) {
                Operators.ignoreExceptions(() -> Operators
                    .allOf(scheduledEvents
                        .get()
                        .stream()
                        .map(event -> event.run(now)))
                    .toCompletableFuture()
                    .get());

                scheduledEvents.getAndUpdate(events -> events
                    .stream()
                    .filter(event -> event.isActive(now))
                    .collect(Collectors.toList()));

                now = now.plus(Duration.ofMinutes(1));
            }
        });

        return kill;
    }

    public void startSingleTime(Duration delay, Creator<CompletionStage<Done>> operation) {
        scheduledEvents.updateAndGet(events -> {
            events.add(SingleEvent.apply(operation, now.plus(delay)));
            return events;
        });
    }

    public void startPeriodicTimer(Duration interval, Creator<CompletionStage<Done>> operation) {
        scheduledEvents.updateAndGet(events -> {
            events.add(PeriodicEvent.apply(operation, interval));
            return events;
        });
    }

    public void startPeriodicTimer(Duration interval, Consumer<CompletableFuture<Done>> operation) {
        startPeriodicTimer(interval, () -> {
            var done = new CompletableFuture<Done>();
            operation.accept(done);
            return done;
        });
    }

    public void startPeriodicTimer(Duration interval, Runnable operation) {
        startPeriodicTimer(interval, done -> {
            operation.run();
            done.complete(Done.getInstance());
        });
    }

}
