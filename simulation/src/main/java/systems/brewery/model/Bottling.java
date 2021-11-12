package systems.brewery.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Bottling {

    /**
     * The moment when the bottling was executed.
     */
    Instant bottled;

    /**
     * The best before date of the bottles.
     */
    Instant bestBefore;

    /**
     * The overall quantity (in litres) of filled up beer.
     */
    int quantity;

    /**
     * The number of bottles which have been bottled.
     */
    int bottles;

}
