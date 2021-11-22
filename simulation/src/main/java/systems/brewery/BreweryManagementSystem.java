package systems.brewery;

import common.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import systems.brewery.ports.BreweryRepositoryJdbcImpl;

@AllArgsConstructor(staticName = "apply")
public final class BreweryManagementSystem {
    private final Brewery brewery;

    public static BreweryManagementSystem apply(){
        var brewery = Brewery.apply(null, null, null, 0);
        return BreweryManagementSystem.apply(brewery);
    }
    public Brewery getBrewery() {
        return brewery;
    }

    public Recipes getRecipes() {
        return Recipes.apply();
    }

    public IngredientProducts getIngredientProducts() {
        var databaseConfig = DatabaseConfiguration.apply("jdbc:postgresql://localhost:5432/brewery", "postgres", "password");
        var repository = BreweryRepositoryJdbcImpl.apply(databaseConfig);
        return IngredientProducts.apply(repository);
    }

}
