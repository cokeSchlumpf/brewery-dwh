package systems.brewery.values.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;

@Value
@AllArgsConstructor(staticName = "apply")
public class Sparge implements Instruction {

    /**
     * The duration of the sparging.
     */
    Duration duration;

}
