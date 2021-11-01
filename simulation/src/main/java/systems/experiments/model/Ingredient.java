package systems.experiments.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Ingredient {

    String name;

    String description;

    int amount;

    String unit;

}
