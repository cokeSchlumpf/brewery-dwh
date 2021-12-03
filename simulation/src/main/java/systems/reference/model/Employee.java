package systems.reference.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Employee {

    String id;

    String firstname;

    String name;

    Instant dateOfBirth;

    String position;

    public static List<Employee> predefined() {
        return Lists.newArrayList(
            johnny(),
            mike());
    }

    public static Employee johnny() {
        return apply("johnny", "Johnny", "Goldgreen", Instant.now(), "Founder 2");
    }

    public static Employee mike() {
        return apply("mike", "Mike", "Handigan", Instant.now(), "Founder 2");
    }

}
