package systems.brewery.model.event;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Boiled {

    Instant start;

    Instant end;

}
