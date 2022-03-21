package systems.brewery.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import systems.brewery.values.event.BrewEvent;
import systems.reference.model.Employee;
import systems.sales.values.Bottling;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Brew {

    /**
     * The recipe the brew is following.
     */
    Recipe beer;

    /**
     * The brewer who was doing the brew.
     */
    Employee brewer;

    /**
     * The moment when the brew process started.
     */
    Instant start;

    /**
     * The moment when the brew process finished.
     */
    Instant end;

    /**
     * I have no idea what that is, but some value which can be measured in the beginning.
     */
    Double originalGravity;

    /**
     * Same here, no glue. But obviously it can be measured in the end.
     */
    Double finalGravity;

    /**
     * The list of events/ actions executed during the brew.
     */
    List<BrewEvent> events;

    public static Brew apply(Recipe beer, Employee brewer, Instant start, double originalGravity) {
        return apply(beer, brewer, start, null, originalGravity, null, List.of());
    }

    public Optional<Instant> getEnd() {
        return Optional.ofNullable(end);
    }

    public Optional<Double> getFinalGravity() {
        return Optional.ofNullable(finalGravity);
    }

    public Brew withEvent(BrewEvent event) {
        var events = new ArrayList<>(this.events);
        events.add(event);

        return withEvents(List.copyOf(events));
    }

}
