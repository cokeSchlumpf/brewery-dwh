package systems.sales;

import systems.brewery.values.Ingredient;
import systems.sales.values.Beer;
import systems.sales.values.Bottling;
import systems.sales.values.Product;

import java.util.List;
import java.util.Optional;

public interface Beers {
    /*
     * create
     */
    void insertBeer(Beer beer);

    void insertBeerProduct(String beer_id,Product product);

    void logBottling(String product_name, Bottling bottling);

    /*
     * read
     */
    List<Product> readBeerProductsByBeerId(String beer_id);

    List<Product> readBeerProducts();

    /*
     * update
     */
    void updateBottling(String productname, int bottles);

    /*
     * delete
     */

    void clear();

}
