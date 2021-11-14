package systems.brewery.values.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;

@Value
@AllArgsConstructor(staticName = "apply")
public class Boil {

    /**
     * The duration in minutes of the boiling process.
     */
    Duration duration;

}
