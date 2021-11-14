package simulation.clock;

import akka.Done;
import akka.japi.Creator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import simulation.Operators;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SingleEvent implements ScheduledEvent {

    private final Creator<CompletionStage<Done>> operation;

    private final LocalDateTime runAfter;

    private boolean executed;

    public static SingleEvent apply(Creator<CompletionStage<Done>> operation, LocalDateTime runAfter) {
        return new SingleEvent(operation, runAfter, false);
    }

    @Override
    public CompletionStage<Done> run(LocalDateTime dateTime) {
        if (!dateTime.isBefore(runAfter)) {
            return Operators.suppressExceptions(operation::create).thenApply(done -> {
                this.executed = true;
                return done;
            });
        } else {
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }

    @Override
    public boolean isActive(LocalDateTime dateTime) {
        return !executed;
    }

}
