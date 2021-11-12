package systems.brewery.model.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Sparge implements Instruction {

    /**
     * The duration of the sparging.
     */
    int duration;

}
