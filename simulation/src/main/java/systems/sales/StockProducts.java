package systems.sales;

import systems.sales.values.StockProduct;
import systems.sales.values.Bottling;
import systems.sales.values.Product;

import java.util.List;
import java.util.Optional;

public interface StockProducts {

    /**
     * Adds new bottles to the stock.
     *
     * @param product The product which should be updated.
     * @param count the number of bottles
     */
    void addToStock(Product product, int count);

    /*
     * create
     */
    void insertBeerProduct(Product product);

    /**
     * This method logs/ inserts information about bottling
     *
     * @param bottling The details of the bottling event.
     */
    void insertBottling(Bottling bottling);

    /**
     * This method removes bottles of a product from stock (and reduces the number of reserved bottles).
     *
     * @param product The product to be taken.
     * @param count   The number of bottles to be taken.
     */
    void removeFromStock(Product product, int count);

    /**
     * This method updates stock information to reserve beers when an order is created.
     *
     * @param product The product which should be reserved.
     * @param count The number of bottles.
     */
    void reserveOnStock(Product product, int count);

    /*
     * read
     */
    Optional<Integer> findProductId(Product product);

    default Integer getProductId(Product product) {
        return findProductId(product).orElseThrow();
    }

    Optional<Product> findProductById(int id);

    default Product getProductById(int id) {
        return findProductById(id).orElseThrow();
    }

    List<Product> findBeerProductsByBeerId(String beerId);

    List<Product> listAllBeerProducts();

    Optional<StockProduct> findAvailableProductByProductName(String productName);

    List<StockProduct> listAvailableProducts();

    /*
     * delete
     */
    void clear();

}
