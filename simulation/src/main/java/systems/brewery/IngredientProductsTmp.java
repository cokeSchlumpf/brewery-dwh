package systems.brewery;

import lombok.AllArgsConstructor;
import systems.brewery.ports.BreweryRecipesRepositoryJdbcImpl;
import systems.brewery.values.IngredientProduct;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class IngredientProductsTmp {

    private final BreweryRecipesRepositoryJdbcImpl repository;

    public Optional<IngredientProduct> find(String producerName, String productName) {
        return repository.getIngredientProductByName(producerName, productName);
    }


    public List<IngredientProduct> findByIngredientName(String ingredientName) {
        return repository.findIngredientProductByIngredientName(ingredientName);

    }


    public Optional<IngredientProduct> get(String productName, String producerName) {
        return repository.getIngredientProductByName(producerName, productName);
    }

    public List<IngredientProduct> list() {
        return repository.selectAllIngredientProducts();
    }

    public void insertIngredientProduct(IngredientProduct ingredientProduct) {
        repository.insertIngredientProduct(ingredientProduct);
    }

    public void remove(String productName, String producerName) {
        repository.removeIngredientProduct(productName, producerName);
    }

}
