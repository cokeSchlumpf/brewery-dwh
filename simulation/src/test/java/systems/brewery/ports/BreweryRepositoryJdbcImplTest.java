package systems.brewery.ports;

import common.DatabaseConfiguration;
import org.junit.Test;
import systems.brewery.values.Ingredient;
import systems.brewery.values.RecipeProperties;

import java.time.Instant;

public class BreweryRepositoryJdbcImplTest {

    @Test
    public void test() {
        var config = DatabaseConfiguration.apply("jdbc:postgresql://localhost:5432/brewery", "postgres", "password");
        var repository = BreweryRepositoryJdbcImpl.apply(config);

        repository.insertIngredient(Ingredient.apply("Mehl", "g"));
        repository.insertRecipe(RecipeProperties.apply("beer", "Beer", "johnny", Instant.now(), Instant.now()));
    }

}
