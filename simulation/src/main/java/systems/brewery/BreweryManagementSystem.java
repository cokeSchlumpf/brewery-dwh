package systems.brewery;

import lombok.AllArgsConstructor;

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
        return IngredientProducts.apply();
    }

}
