package systems.brewery.values.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Rest implements Instruction {

    /**
     * Duration in minutes.
     */
    int duration;

}
