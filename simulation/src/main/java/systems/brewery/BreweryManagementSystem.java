package systems.brewery;

import common.configs.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class BreweryManagementSystem {

    private final Ingredients ingredients;

    private final IngredientProducts ingredientProducts;

    private final Recipes recipes;

    private final Brews brews;

    public static BreweryManagementSystem apply(DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        var ingredients = IngredientsJdbcImpl.apply(jdbi);
        var ingredientProducts = IngredientProductsJdbcImpl.apply(jdbi, ingredients);
        var recipes = RecipesJdbcImpl.apply(jdbi, ingredients);
        var brews = BrewsJdbcImpl.apply(jdbi, ingredientProducts, recipes);

        return apply(ingredients, ingredientProducts, recipes, brews);
    }

}
