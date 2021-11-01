package systems.reference.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Employee {

    String id;

    String firstname;

    String name;

    Instant dateOfBirth;

    String text;

}
