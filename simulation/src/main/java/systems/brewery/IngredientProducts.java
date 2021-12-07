package systems.brewery;

import systems.brewery.values.IngredientProduct;

import java.util.List;
import java.util.Optional;

public interface IngredientProducts {

    /*
     * create
     */
    void insertIngredientProduct(IngredientProduct ingredientProduct);

    /*
     * read
     */
    List<IngredientProduct> selectAllIngredientProducts();

    Optional<IngredientProduct> getIngredientProductByName(String producerName, String productName);

    int getIngredientProductIdByName(String producerName, String productName);

    List<IngredientProduct> findIngredientProductByIngredientName(String IngredientName);

    /*
     * delete
     */
    void removeIngredientProduct(String productName, String producerName);

    void clear();

}
