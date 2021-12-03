package systems.brewery.ports;

import common.configs.DatabaseConfiguration;
import org.junit.Test;
import systems.brewery.values.Ingredient;

public class BreweryRepositoryJdbcImplTest {

    @Test
    public void test() {
        var config = DatabaseConfiguration.apply("jdbc:postgresql://localhost:5432/brewery", "postgres", "password");
        var repository = BreweryRecipesRepositoryJdbcImpl.apply(config);

        repository.insertIngredient(Ingredient.apply("Mehl", "g"));
    }

}
