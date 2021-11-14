package systems.brewery.values;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Ingredient {

    String name;

    String unit;

}
