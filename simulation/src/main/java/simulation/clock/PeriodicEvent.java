package simulation.clock;

import akka.Done;
import akka.japi.Creator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import simulation.Operators;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PeriodicEvent implements ScheduledEvent {

    private final Creator<CompletionStage<Done>> operation;

    private final Duration interval;

    private LocalDateTime runAfter;

    public static PeriodicEvent apply(Creator<CompletionStage<Done>> operation, Duration interval) {
        return new PeriodicEvent(operation, interval, LocalDateTime.MIN);
    }

    @Override
    public CompletionStage<Done> run(LocalDateTime dateTime) {
        if (!dateTime.isBefore(runAfter)) {
            return Operators.suppressExceptions(operation::create).thenApply(done -> {
                this.runAfter = dateTime.plus(interval);
                return done;
            });
        } else {
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }

    @Override
    public boolean isActive(LocalDateTime dateTime) {
        return true;
    }

}
