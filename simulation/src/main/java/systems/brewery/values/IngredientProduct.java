package systems.brewery.values;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A ingredient product is a concrete "implementation" or product which can be used as the ingredient.
 * E.g. Rice is an ingredient, Uncle Ben's Rice is the product.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class IngredientProduct {

    /**
     * The ingredient which is realized by this product.
     */
    Ingredient ingredient;

    /**
     * The id (order number) of the product, provided/ owned by the producer of the product.
     */
    String producerProductId;

    /**
     * The name of the producer.
     */
    String producerName;

    /**
     * The name of the product as named by the producer of the product.
     */
    String productName;

}
