package simulation.clock;

import akka.Done;
import akka.japi.Creator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import simulation.Operators;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PeriodicEvent implements ScheduledEvent {

    private final String key;

    private final Creator<CompletionStage<Done>> operation;

    private final Duration interval;

    private LocalDateTime runAfter;

    public static PeriodicEvent apply(String key, Creator<CompletionStage<Done>> operation, Duration interval) {
        return new PeriodicEvent(key, operation, interval, LocalDateTime.MIN);
    }

    @Override
    public CompletionStage<Done> run(LocalDateTime dateTime) {
        return Operators.suppressExceptions(operation::create).thenApply(done -> {
            this.runAfter = dateTime.plus(interval);
            return done;
        });
    }

    @Override
    public boolean shouldRun(LocalDateTime dateTime) {
        return !dateTime.isBefore(runAfter);
    }

    @Override
    public boolean isActive(LocalDateTime dateTime) {
        return true;
    }

    @Override
    public String getKey() {
        return key;
    }
}
