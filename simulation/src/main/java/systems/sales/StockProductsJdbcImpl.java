package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Bottling;
import systems.sales.values.Product;
import systems.sales.values.StockProduct;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class StockProductsJdbcImpl implements StockProducts {

    private final Jdbi jdbi;

    private Optional<Integer> readBeerProductIdByName(String product_name){

        var query = Templates.renderTemplateFromResources("db/sql.sales/products--select-id-by-name.sql");
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_name", product_name)
                .mapTo(Integer.class)
                .findOne());
    }

    @Override
    public void addToStock(Product product, int count) {

    }

    @Override
    public void insertBeerProduct(Product product) {
        var query = Templates.renderTemplateFromResources("db/sql.sales/products--insert.sql");
        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("beer_id", product.getBeerId())
            .bind("product_name", product.getProductName())
            .bind("price", product.getPrice())
            .bind("volume", product.getVolume())
            .execute()
        );
    }

    @Override
    public void insertBottling(Bottling bottling) {
        var product_id = readBeerProductIdByName(bottling.getProduct().getProductName());

        if(product_id.isEmpty()){
            throw new RuntimeException("Product with product_id " + product_id + "does not exist in system.");
        }
        else {
            var query = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--insert.sql");
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("product_id", product_id)
                .bind("bottled", bottling.getBottled())
                .bind("best_before_date", bottling.getBestBefore())
                .bind("bottles", bottling.getBottles())
                .execute());
        }
    }

    @Override
    public void removeFromStock(Product product, int count) {

    }

    @Override
    public void reserveOnStock(Product product, int count) {

    }

    @Override
    public List<Product> findBeerProductsByBeerId(String beerId){

        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--products--select-by-beer-id.sql");

        var products = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("beer_id", beerId)
                .map((rs, ctx) -> {
                    return Product.apply(rs.getString("product_name"), rs.getString("beer_id"), rs.getDouble("price"), rs.getDouble("volume"));
                })
                .list());

        return products;

    }

    private Optional<Product> readBeerProductByName(String product_name){

        var existingId = readBeerProductIdByName(product_name);
        var query = Templates.renderTemplateFromResources("db/sql.sales/products--select-by-id.sql");

        var product = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_id", existingId)
                .map((rs,ctx) ->{
                    return Product.apply(rs.getString("product_name"), rs.getString("beer_id"), rs.getDouble("price"), rs.getDouble("volume"));
                })
                .findFirst());
        return product;

    }

    @Override
    public List<Product> listAllBeerProducts(){
        var query =Templates.renderTemplateFromResources("db/sql.sales/products--select-all.sql");

        var products = jdbi.withHandle(handle -> handle
                                            .createQuery(query)
                                            .map((rs,ctx) -> Product.apply(rs.getString("product_name"),rs.getString("beer_id"), rs.getDouble("price"), rs.getDouble("volume")))
                                            .list());
        return products;
    }

    @Override
    public Optional<StockProduct> findAvailableProductByProductName(String productName) {
        return Optional.empty();
    }

    @Override
    public List<StockProduct> listAvailableProducts() {
        return null;
    }

     /*
    private List<Bottling> readBottlings(int product_id){
        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--select-by-productID.sql");

        var bottlings = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_id", product_id)
                .map((rs, ctx) -> {
                    return rs.getInt("id");
                })
                .stream()
                .map(this::readBottling)
                .filter((obj)-> !obj.isEmpty())
                .map((bot) -> bot.get())
                .collect(Collectors.toList()));
        return bottlings;
    }


    private Optional<Bottling> readBottling(int bottling_id){
        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--select-by-id.sql");

        var result = jdbi.withHandle(handle -> handle
                                    .createQuery(query)
                                    .bind("id", bottling_id)
                                    .map((rs, ctx) -> {
                                        return Bottling.apply(

                                                rs.getTimestamp("bottled").toInstant(),
                                                rs.getTimestamp("best_before_date").toInstant(),
                                                rs.getInt("quantity"),
                                                rs.getInt("bottles"));
                                    })
                                    .findFirst());
        return result;
    }
     */


    @Override
    public void clear() {
        this.clearBottlings();
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

    private void clearBottlings() {
        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--delete-all.sql");

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .execute());
    }


}
