package systems.brewery.values.event;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Mashed {

    /**
     * The moment mashing started.
     */
    Instant start;

    /**
     * The moment mashing ended.
     */
    Instant end;

    /**
     * The temperature when mashing started.
     */
    double startTemperature;

    /**
     * The temperature when mashing ended.
     */
    double endTemperature;

}
