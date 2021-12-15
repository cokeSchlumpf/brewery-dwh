package systems.sales;

import systems.brewery.values.Ingredient;
import systems.sales.values.Beer;
import systems.sales.values.Product;

import java.util.List;
import java.util.Optional;

public interface Beers {
    /*
     * create
     */
    void insertBeer(Beer beer);

    /*
     * read
     */

    int getBeerIdByName(String name);

    Optional<Beer> findBeerByName(String beer_name);

    List<Product> getBeerProducts();

    /*
     * update
     */
    void updateBeerProduct(String productname, int bottles);

    /*
     * delete
     */
    void removeBeer(String beer);

    void removeProduct(String product);

    void clear();

}
