package systems.brewery.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.values.instructions.Instruction;
import systems.reference.model.Employee;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Recipe {

    String beerKey;

    String beerName;

    Employee productOwner;

    Instant created;

    Instant updated;

    List<Instruction> instructions;

    public static Recipe apply(RecipeProperties properties, List<Instruction> instructions) {
        return Recipe.apply(
            properties.getBeerKey(),
            properties.getBeerName(),
            properties.getProductOwner(),
            properties.getCreated(),
            properties.getCreated(),
            instructions);
    }

    public RecipeProperties getProperties() {
        return RecipeProperties.apply(beerKey, beerName, productOwner, created, updated);
    }

}
