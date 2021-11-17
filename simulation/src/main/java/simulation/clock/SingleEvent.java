package simulation.clock;

import akka.Done;
import akka.japi.Creator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import simulation.Operators;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SingleEvent implements ScheduledEvent {

    private final String key;

    private final Creator<CompletionStage<Done>> operation;

    private final LocalDateTime runAfter;

    private boolean executed;

    public static SingleEvent apply(String key, Creator<CompletionStage<Done>> operation, LocalDateTime runAfter) {
        return new SingleEvent(key, operation, runAfter, false);
    }

    @Override
    public CompletionStage<Done> run(LocalDateTime dateTime) {
        return Operators.suppressExceptions(operation::create).thenApply(done -> {
            this.executed = true;
            return done;
        });
    }

    @Override
    public boolean shouldRun(LocalDateTime dateTime) {
        return !dateTime.isBefore(runAfter);
    }

    @Override
    public boolean isActive(LocalDateTime dateTime) {
        return !executed;
    }

    @Override
    public String getKey() {
        return key;
    }
}
