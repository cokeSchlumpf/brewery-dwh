package systems.sales.values;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Customer {

    int id;

    String email;

    String firstname;

    String name;

    Address address;

}
