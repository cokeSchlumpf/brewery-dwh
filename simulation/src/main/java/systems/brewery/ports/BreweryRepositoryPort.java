package systems.brewery.ports;

import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;
import systems.brewery.values.RecipeProperties;

import java.util.List;
import java.util.Optional;

public interface BreweryRepositoryPort {

    void insertRecipe(RecipeProperties recipe);

    Optional<RecipeProperties> findRecipeByName(String beerKey);

    void updateRecipe(RecipeProperties recipe);

    void removeRecipe(String beerName);

    void insertIngredient(Ingredient ingredient);

    void removeIngredient(String ingredient);

    Optional<Ingredient> getIngredientByName(String name);

    void insertIngredientProduct(IngredientProduct ingredientProduct);

    List<IngredientProduct> selectAllIngredientProducts();

    Optional<IngredientProduct> getIngredientProductByName(String producerName, String productName);

    List<IngredientProduct> findIngredientProductByIngredientName(String IngredientName);

    void updateIngredientProduct(IngredientProduct ingredientProduct);

    void removeIngredientProduct(String productName, String producerName);

}
