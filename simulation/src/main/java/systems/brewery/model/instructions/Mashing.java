package systems.brewery.model.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Mashing implements Instruction {

    int startTemperature;

    int endTemperature;

    int duration;

}
