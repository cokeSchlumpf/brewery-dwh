package systems.brewery.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import systems.brewery.values.event.BrewEvent;
import systems.reference.model.Employee;

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
    Integer originalGravity;

    /**
     * Same here, no glue. But obviously it can be measured in the end.
     */
    Integer finalGravity;

    /**
     * The list of events/ actions executed during the brew.
     */
    List<BrewEvent> events;

    /**
     * Bottling of create beer.
     */
    List<Bottling> bottlings;

    public static Brew apply(Recipe beer, Employee brewer, Instant start, int originalGravity) {
        return apply(beer, brewer, start, null, originalGravity, null, List.of(), List.of());
    }

    public Optional<Instant> getEnd() {
        return Optional.ofNullable(end);
    }

    public Optional<Integer> getFinalGravity() {
        return Optional.ofNullable(finalGravity);
    }

    public Brew withBottling(Bottling bottling) {
        var bottlings = new ArrayList<>(this.bottlings);
        bottlings.add(bottling);

        return withBottlings(bottlings);
    }

    public Brew withEvent(BrewEvent event) {
        var events = new ArrayList<>(this.events);
        events.add(event);

        return withEvents(List.copyOf(events));
    }

}
