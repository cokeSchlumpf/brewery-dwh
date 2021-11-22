package systems.brewery;

import lombok.AllArgsConstructor;
import systems.brewery.ports.BreweryRepositoryJdbcImpl;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class IngredientProducts {

    private final List<IngredientProduct> products;

    private final BreweryRepositoryJdbcImpl repository;


    public static IngredientProducts apply(BreweryRepositoryJdbcImpl repository) {
        var products = new ArrayList<IngredientProduct>();
        var coffee = Ingredient.apply("coffee", "g");
        var coke = Ingredient.apply("coke", "l");

        products = new ArrayList<>();
        products.add(IngredientProduct.apply(coffee, "123", "Tchibo", "Kaffee"));
        products.add(IngredientProduct.apply(coffee, "123", "Mokka", "Fix"));
        products.add(IngredientProduct.apply(coke, "123", "Pepsi", "Cola"));
        products.add(IngredientProduct.apply(coke, "123", "Coca", "Cola"));
        products.add(IngredientProduct.apply(coke, "123", "Vita", "Cola"));

        return apply(products, repository);
    }

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

    public void registerOrUpdate(IngredientProduct ingredientProduct) {
        /*
        repository
                .getIngredientProductByName(ingredientProduct.getProducerName(), ingredientProduct.getProductName())
                .ifPresentOrElse(
                        ip -> repository.updateIngredientProduct(ingredientProduct),
                        () -> repository.insertIngredientProduct(ingredientProduct)
                );

         */
        repository.insertIngredientProduct(ingredientProduct);
    }

    public void remove(String productName, String producerName) {
        repository.removeIngredientProduct(productName, producerName);
    }

    public void renameProduct(String currentProducerProductName, String producerName, String newProducerProductName) {
        IngredientProduct ip = repository.getIngredientProductByName(producerName, currentProducerProductName).get();
        repository.updateIngredientProduct(IngredientProduct.apply(ip.getIngredient(), ip.getProducerProductId(), ip.getProducerName(), newProducerProductName));

    }

    public void updateProductId(String producerName, String productName, String producerProductId) {
        IngredientProduct ip = repository.getIngredientProductByName(producerName, productName).get();
        repository.updateIngredientProduct(IngredientProduct.apply(ip.getIngredient(), producerProductId, ip.getProducerName(), ip.getProductName()));
    }

}
