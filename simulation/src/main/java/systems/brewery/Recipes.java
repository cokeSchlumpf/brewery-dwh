package systems.brewery;

import systems.brewery.values.Recipe;

import java.util.Optional;

public interface Recipes {

    void insertRecipe(Recipe recipe);

    Optional<Recipe> findRecipeByName(String beerKey);

    default Recipe getRecipeByName(String beerKey) {
        return findRecipeByName(beerKey).orElseThrow();
    }

    void removeRecipe(String beerName);

    void clear();

}
