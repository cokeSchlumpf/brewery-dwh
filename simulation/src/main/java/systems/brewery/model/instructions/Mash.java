package systems.brewery.model.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;

@Value
@AllArgsConstructor(staticName = "apply")
public class Mash implements Instruction {

    /**
     * The start temperature of the beer at the beginning of the mashing.
     */
    double startTemperature;

    /**
     * The expected start temperature of the beer at the end of the mashing.
     */
    double endTemperature;

    /**
     * The duration of the mashing process.
     */
    Duration duration;

}
