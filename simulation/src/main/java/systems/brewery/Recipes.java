package systems.brewery;

import lombok.AllArgsConstructor;
import systems.brewery.values.Ingredient;
import systems.brewery.values.Recipe;
import systems.brewery.values.instructions.AddIngredient;
import systems.brewery.values.instructions.Boil;
import systems.brewery.values.instructions.Instruction;
import systems.reference.model.Employee;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class Recipes {

    public Recipe get(String beerKey) {
        var owner = Employee.apply("123", "Egon", "Olsen", Instant.MIN, "foo bar?");
        var instructions = new ArrayList<Instruction>();

        instructions.add(AddIngredient.apply(Ingredient.apply("coffee", "g"), 10));
        instructions.add(AddIngredient.apply(Ingredient.apply("coke", "l"), 3));
        instructions.add(Boil.apply(Duration.ofHours(4)));
        instructions.add(Boil.apply(Duration.ofHours(8)));

        return Recipe.apply(beerKey, beerKey, owner, Instant.now(), Instant.now(), instructions);
    }

    public Optional<Recipe> find(String key) {
        throw new RuntimeException("not implemented");
    }

    public List<String> list() {
        throw new RuntimeException("not implemented");
    }

    public void remove(String beerKey) {
        throw new RuntimeException("not implemented");
    }

    public void register(String beerKey, String beerName, String productOwner, List<Instruction> instructions) {
        throw new RuntimeException("not implemented");
    }

    public void updateInstructions(String beerKey, List<Instruction> instructions) {
        throw new RuntimeException("not implemented");
    }

    public void updateName(String beerKey, String beerName) {
        throw new RuntimeException("not implemented");
    }

    public void updateProductOwner(Employee productOwner) {
        throw new RuntimeException("not implemented");
    }

}
