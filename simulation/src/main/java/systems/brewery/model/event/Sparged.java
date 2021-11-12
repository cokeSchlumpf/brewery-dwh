package systems.brewery.model.event;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Sparged {

    Instant start;

    Instant end;

}
