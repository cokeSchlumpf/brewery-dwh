package systems.brewery.values.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;

@Value
@AllArgsConstructor(staticName = "apply")
public class Rest implements Instruction {

    /**
     * Duration in minutes.
     */
    Duration duration;

}
