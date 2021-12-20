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
     * The product which is bottled.
     */
    Product product;

    /**
     * The moment when the bottling was executed.
     */
    Instant bottled;

    /**
     * The best before date of the bottles.
     */
    Instant bestBefore;

    /**
     * The number of bottles which have been bottled.
     */
    int bottles;

    public static List<Bottling> predefinedBottlings(Product product){
        var bottlings = Lists.<Bottling>newArrayList();
        bottlings.add(Bottling.apply(product, Instant.now(), Instant.now().plus(P.randomDuration(Duration.ofDays(90),Duration.ofDays(30))),40));
        bottlings.add(Bottling.apply(product, Instant.now(), Instant.now().plus(P.randomDuration(Duration.ofDays(90),Duration.ofDays(30))), 10));
        bottlings.add(Bottling.apply(product, Instant.now(), Instant.now().plus(P.randomDuration(Duration.ofDays(90),Duration.ofDays(30))), 5));
        return bottlings;
    }
}
