package systems.brewery.ports;

import systems.brewery.values.Ingredient;
import systems.brewery.values.RecipeProperties;

import java.util.Optional;

public interface BreweryRepositoryPort {

    void insertRecipe(RecipeProperties recipe);

    Optional<RecipeProperties> findRecipeByName(String beerKey);

    void removeRecipe(String beerName);

    void insertIngredient(Ingredient ingredient);

    void removeIngredient(String ingredient);

    Optional<Ingredient> getIngredientByName(String name);

}
