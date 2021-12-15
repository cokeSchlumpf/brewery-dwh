package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Beer;
import systems.sales.values.Product;

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


    @Override
    public int getBeerIdByName(String name) {
        return 0;
    }

    @Override
    public void updateBeerProduct(Product product) {
        var existingId = getBeerProductIdByName(product.getProductName());

        if (existingId.isEmpty()) {
            throw new RuntimeException(String.format("Beer product does not exist"));
        } else {
            //var query = Templates.renderTemplateFromResources("db/sql/brewery/ingredients--update.sql");

        }
    }

    private Optional<Integer> getBeerProductIdByName(String product_name){

        var query = Templates.renderTemplateFromResources("db/sql.sales/products--select-id-by-name.sql");

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_name", product_name)
                .mapTo(Integer.class)
                .findOne());
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
