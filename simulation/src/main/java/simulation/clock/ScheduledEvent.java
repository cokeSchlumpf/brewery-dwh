package simulation.clock;

import akka.Done;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

public interface ScheduledEvent {

    CompletionStage<Done> run(LocalDateTime dateTime);

    boolean isActive(LocalDateTime dateTime);

}
