package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Beer;
import systems.sales.values.Product;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class BeersJdbcImpl implements Beers{

    private final Jdbi jdbi;

    @Override
    public void insertBeer(Beer beer) {
        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--insert.sql");

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("beer_id", beer.getBeerKey())
                .bind("beer_name", beer.getBeerName())
                .execute());

        for (int i = 0; i < beer.getProducts().size(); i++) {
            this.insertBeerProduct(beer, beer.getProducts().get(i));
        }
    }

    @Override
    public int getBeerIdByName(String name) {
        return 0;
    }

    @Override
    public Optional<Beer> findBeerByName(String beer_name) {
        return Optional.empty();
    }

    public void insertBeerProduct(Beer beer,Product product) {
        var query = Templates.renderTemplateFromResources("db/sql.sales/products--insert.sql");
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("beer_key", beer.getBeerKey())
                .bind("product_name", product.getProductName())
                .bind("price", product.getPrice())
                .bind("volume", product.getVolume())
                .bind("inventory", product.getInventory())
                .execute()
                );
    }

    private Optional<Integer> getBeerProductIdByName(String product_name){

        var query = Templates.renderTemplateFromResources("db/sql.sales/products--select-id-by-name.sql");

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_name", product_name)
                .mapTo(Integer.class)
                .findOne());
    }

    public Optional<Product> findBeerProductByName(String product_name){

        var existingId = getBeerProductIdByName(product_name);
        var query = Templates.renderTemplateFromResources("db/sql.sales/products--select-by-id.sql");

        var product = jdbi.withHandle(handle -> handle
                                .createQuery(query)
                                .bind("product_id", existingId)
                                .map((rs,ctx) ->{
                                    return Product.apply(rs.getString("product_name"),rs.getDouble("price"), rs.getDouble("volume"), rs.getInt("inventory"));
                                })
                                .findFirst());
        return product;

    }

    @Override
    public List<Product> getBeerProducts(){
        var query =Templates.renderTemplateFromResources("db/sql.sales/products--select-all.sql");

        var products = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .map((rs,ctx) ->{
                    return Product.apply(rs.getString("product_name"),rs.getDouble("price"), rs.getDouble("volume"), rs.getInt("inventory"));
                })
                .list());
        return products;
    }

    @Override
    public void updateBeerProduct(String product_name, int bottles) {
        var existingId = getBeerProductIdByName(product_name);
        if (existingId.isEmpty()) {
            throw new RuntimeException(String.format("Beer product does not exist"));
        } else {
            var current_inventory = findBeerProductByName(product_name).get().getInventory();
            var query = Templates.renderTemplateFromResources("db/sql.sales/products--update.sql");

            jdbi.withHandle(handle -> handle
                    .createUpdate(query)
                    .bind("product_name", product_name)
                    .bind("inventory", current_inventory + bottles)
                    .bind("product_id", existingId)
                    .execute());

        }
    }


    @Override
    public void removeBeer(String beer) {

    }

    @Override
    public void removeProduct(String product) {

    }

    @Override
    public void clear() {
        this.clearProducts();

        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--delete-all.sql");
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .execute());
    }

    private void clearProducts(){
        var query = Templates.renderTemplateFromResources("db/sql.sales/products--delete-all.sql");
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .execute());
    }
}
