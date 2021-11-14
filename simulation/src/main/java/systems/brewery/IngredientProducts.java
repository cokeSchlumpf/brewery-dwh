package systems.brewery;

import systems.brewery.values.IngredientProduct;

import java.util.List;
import java.util.Optional;

public final class IngredientProducts {

    public Optional<IngredientProduct> find(String productName, String producerName) {
        throw new RuntimeException("not implemented");
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
