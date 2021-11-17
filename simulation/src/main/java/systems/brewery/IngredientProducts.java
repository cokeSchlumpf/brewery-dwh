package systems.brewery;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class IngredientProducts {

    private final List<IngredientProduct> products;

    public static IngredientProducts apply() {
        var products = new ArrayList<IngredientProduct>();
        var coffee = Ingredient.apply("coffee", "g");
        var coke = Ingredient.apply("coke", "l");

        products = new ArrayList<>();
        products.add(IngredientProduct.apply(coffee, "123", "Tchibo", "Kaffee"));
        products.add(IngredientProduct.apply(coffee, "123", "Mokka", "Fix"));
        products.add(IngredientProduct.apply(coke, "123", "Pepsi", "Cola"));
        products.add(IngredientProduct.apply(coke, "123", "Coca", "Cola"));
        products.add(IngredientProduct.apply(coke, "123", "Vita", "Cola"));

        return apply(products);
    }

    public Optional<IngredientProduct> find(String productName, String producerName) {
        throw new RuntimeException("not implemented");
    }

    public List<IngredientProduct> findByIngredientName(String ingredientName) {
        return products
            .stream()
            .filter(p -> p.getIngredient().getName().equals(ingredientName))
            .collect(Collectors.toList());
    }

    public IngredientProduct get(String productName, String producerName) {
        throw new RuntimeException("not implemented");
    }

    public List<IngredientProduct> list() {
        throw new RuntimeException("not implemented");
    }

    public void register(String productName, String producerName, String producerProductId, String ingredientName) {
        throw new RuntimeException("not implemented");
    }

    public void remove(String productName, String producerName) {
        throw new RuntimeException("not implemented");
    }

    public void renameProduct(String currentProducerProductName, String producerName, String newProducerProductName) {
        throw new RuntimeException("not implemented");
    }

    public void updateProductId(String producerName, String productName, String producerProductId) {
        throw new RuntimeException("not implemented");
    }

}
