package systems.brewery.model.instructions;

import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.model.Ingredient;

@Value
@AllArgsConstructor(staticName = "apply")
public class AddIngredient implements Instruction {

    /**
     * The ingredient which should be added.
     */
    Ingredient ingredient;

    /**
     * The amount of the ingredient (the unit is part of the ingredient definition).
     */
    double amount;

}
