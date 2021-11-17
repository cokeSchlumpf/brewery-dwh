package systems.brewery;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "apply")
public final class BreweryManagementSystem {

    public Brewery getBrewery() {
        return Brewery.apply(null);
    }

    public Recipes getRecipes() {
        return Recipes.apply();
    }

    public IngredientProducts getIngredientProducts() {
        return IngredientProducts.apply();
    }

}
