package systems.sales;

import systems.brewery.values.Ingredient;
import systems.sales.values.Beer;
import systems.sales.values.Product;

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

    /*
     * update
     */
    void updateBeerProduct(Product product);

    /*
     * delete
     */
    void removeBeer(String beer);

    void removeProduct(String product);

    void clear();

}
