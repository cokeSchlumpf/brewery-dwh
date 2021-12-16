package systems.sales.values;

import com.google.common.collect.Lists;
import common.P;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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

    public static List<Bottling> predefinedBottlings(){
        var bottlings = Lists.<Bottling>newArrayList();
        bottlings.add(Bottling.apply(Instant.now(), Instant.now().plus(P.randomDuration(Duration.ofDays(90),Duration.ofDays(30))),40,40));
        bottlings.add(Bottling.apply(Instant.now(), Instant.now().plus(P.randomDuration(Duration.ofDays(90),Duration.ofDays(30))),40,10));
        bottlings.add(Bottling.apply(Instant.now(), Instant.now().plus(P.randomDuration(Duration.ofDays(90),Duration.ofDays(30))),40,5));
        return bottlings;
    }
}
