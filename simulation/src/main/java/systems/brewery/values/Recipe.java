package systems.brewery.values;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.values.instructions.*;
import systems.reference.model.Employee;

import java.time.Duration;
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

    public static Recipe apply(String beerKey, String beerName, Employee productOwner, List<Instruction> instructions) {
        return apply(beerKey, beerName, productOwner, Instant.now(), Instant.now(), instructions);
    }

    public static List<Recipe> predefined() {
        return Lists.newArrayList(fooBeer(), barBeer());
    }

    public static Recipe fooBeer() {
        var instructions = Lists.<Instruction>newArrayList();
        instructions.add(AddIngredient.apply(Ingredient.water(), 1000));
        instructions.add(AddIngredient.apply(Ingredient.hop(), 30));
        instructions.add(AddIngredient.apply(Ingredient.malt(), 72));
        instructions.add(Mash.apply(25.00, 40.00, Duration.ofHours(4)));
        instructions.add(Rest.apply(Duration.ofHours(2)));
        instructions.add(Mash.apply(40.00, 70.00, Duration.ofHours(3)));
        instructions.add(Rest.apply(Duration.ofHours(1)));
        instructions.add(Mash.apply(70.00, 90.00, Duration.ofHours(5)));
        instructions.add(Rest.apply(Duration.ofHours(3)));
        instructions.add(AddIngredient.apply(Ingredient.lemon(), 12));
        instructions.add(Boil.apply(Duration.ofHours(10)));
        instructions.add(Sparge.apply(Duration.ofHours(3)));

        return Recipe.apply("foo", "Foo Beer", Employee.johnny(), instructions);
    }

    public static Recipe barBeer() {
        var instructions = Lists.<Instruction>newArrayList();
        instructions.add(AddIngredient.apply(Ingredient.water(), 1000));
        instructions.add(AddIngredient.apply(Ingredient.hop(), 38));
        instructions.add(AddIngredient.apply(Ingredient.malt(), 60));
        instructions.add(AddIngredient.apply(Ingredient.beerWort(), 72));
        instructions.add(Mash.apply(25.00, 40.00, Duration.ofHours(3)));
        instructions.add(Rest.apply(Duration.ofHours(3)));
        instructions.add(Mash.apply(40.00, 70.00, Duration.ofHours(2)));
        instructions.add(Rest.apply(Duration.ofHours(2)));
        instructions.add(AddIngredient.apply(Ingredient.glutamate(), 3));
        instructions.add(Boil.apply(Duration.ofHours(4)));
        instructions.add(Sparge.apply(Duration.ofHours(6)));

        return Recipe.apply("bar", "Bar Beer", Employee.mike(), instructions);
    }

}
