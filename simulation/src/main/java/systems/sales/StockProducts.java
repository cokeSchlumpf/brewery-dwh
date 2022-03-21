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
    void insertProduct(Product product);

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

    /**
     * Lookup a product by its name and return its id.
     *
     * @param productName The product name to lookup.
     * @return The id, if found.
     */
    Optional<Integer> findProductId(String productName);

    /**
     * Lookup a product by its name and return its id.
     *
     * @param product The product o lookup.
     * @return The id, if found.
     */
    default Optional<Integer> findProductId(Product product) {
        return findProductId(product.getProductName());
    }

    /**
     * Lookup a product by its id.
     *
     * @param id The product id to search for.
     * @return The product.
     */
    Optional<Product> findProductById(int id);

    /**
     * Lookup a product by its name.
     *
     * @param name The name of the product.
     * @return The product.
     */
    Optional<Product> findProductByName(String name);

    /**
     * Lookup a product in stock by its name.
     *
     * @param productName The name of the product.
     * @return The stock product.
     */
    Optional<StockProduct> findStockProductByName(String productName);

    /**
     * Fetch a list of all known products.
     *
     * @return The products.
     */
    List<Product> getAllProducts();

    /**
     * Fetch a list of all products registered in stock.
     *
     * @return The stock products.
     */
    List<StockProduct> getAllStockProducts();

    /**
     * Lookup a product by its name and return its id.
     *
     * @param productName The product name to lookup.
     * @return The id, if found.
     */
    default Integer getProductId(String productName) {
        return findProductId(productName).orElseThrow();
    }

    /**
     * Lookup a product by its name and return its id.
     *
     * @param product The product o lookup.
     * @return The id, if found.
     */
    default Integer getProductId(Product product) {
        return findProductId(product).orElseThrow();
    }

    /**
     * Lookup a product by its id.
     *
     * @param id The product id to search for.
     * @return The product.
     */
    default Product getProductById(int id) {
        return findProductById(id).orElseThrow();
    }

    /**
     * Lookup a product by its name.
     *
     * @param name The name of the product.
     * @return The product.
     */
    default Product getProductByName(String name) { return findProductByName(name).orElseThrow(); }

    /**
     * Get a list of all products which are base on one type of beer.
     *
     * @param beerId The id of the beer.
     * @return The product.
     */
    List<Product> getProductsByBeerId(String beerId);

    /**
     * Lookup a product in stock by its name.
     *
     * @param productName The name of the product.
     * @return The stock product.
     */
    default StockProduct getStockProductByName(String productName) {
        return findStockProductByName(productName).orElseThrow();
    }

    /**
     * Remove all data from database.
     */
    void clear();

}
