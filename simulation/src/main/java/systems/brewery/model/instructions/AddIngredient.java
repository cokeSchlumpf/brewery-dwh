package systems.brewery.model.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.model.Ingredient;

@Value
@AllArgsConstructor(staticName = "apply")
public class AddIngredient implements Instruction {

    Ingredient ingredient;

    int amount;

}
