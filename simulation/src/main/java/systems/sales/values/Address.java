package systems.sales.values;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Address {

    String street;

    String zipCode;

    String city;

}
