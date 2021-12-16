package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Beer;
import systems.sales.values.Bottling;
import systems.sales.values.Product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            this.insertBeerProduct(beer.getBeerKey(), beer.getProducts().get(i));
        }
    }

    @Override
    public void insertBeerProduct(String beer_id,Product product) {
        var query = Templates.renderTemplateFromResources("db/sql.sales/products--insert.sql");
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("beer_id", beer_id)
                .bind("product_name", product.getProductName())
                .bind("price", product.getPrice())
                .bind("volume", product.getVolume())
                .execute()
                );
        for (int i = 0; i < product.getBottlings().size(); i++) {
            this.logBottling(product.getProductName(),product.getBottlings().get(i));
        }
    }

    @Override
    public void logBottling(String product_name, Bottling bottling) {
        var product_id = readBeerProductIdByName(product_name);

        if(product_id.isEmpty()){
            throw new RuntimeException("Product with product_id " + product_id + "does not exist in system.");
        }
        else {
            var query = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--insert.sql");
            jdbi.withHandle(handle -> handle
                    .createUpdate(query)
                    .bind("product_id", product_id.get())
                    .bind("bottled", bottling.getBottled())
                    .bind("best_before_date", bottling.getBestBefore())
                    .bind("quantity", bottling.getQuantity())
                    .bind("bottles", bottling.getBottles())
                    .execute());
        }
    }

    private Optional<Integer> readBeerProductIdByName(String product_name){

        var query = Templates.renderTemplateFromResources("db/sql.sales/products--select-id-by-name.sql");
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_name", product_name)
                .mapTo(Integer.class)
                .findOne());
    }

    @Override
    public List<Product> readBeerProductsByBeerId(String beer_id){

        var query = Templates.renderTemplateFromResources("db/sql.sales/beers--products--select-by-beer-id.sql");

        var products = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("beer_id", beer_id)
                .map((rs, ctx) -> {
                    var bottlings = readBottlings(rs.getInt("product_id"));
                    return Product.apply(rs.getString("product_name"),rs.getDouble("price"), rs.getDouble("volume"), bottlings);
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
                    var bottlings = readBottlings(existingId.get());
                    return Product.apply(rs.getString("product_name"),rs.getDouble("price"), rs.getDouble("volume"), bottlings);
                })
                .findFirst());
        return product;

    }

    @Override
    public List<Product> readBeerProducts(){
        var query =Templates.renderTemplateFromResources("db/sql.sales/products--select-all.sql");

        var products = jdbi.withHandle(handle -> handle
                                            .createQuery(query)
                                            .map((rs,ctx) ->{
                                                var bottlings = readBottlings(rs.getInt("product_id"));
                                                return Product.apply(rs.getString("product_name"),rs.getDouble("price"), rs.getDouble("volume"), bottlings);

                                            })
                                            .list());
        return products;
    }

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

    @Override
    public void updateBottling(String product_name, int bottles) {
        var product_id = readBeerProductIdByName(product_name);
        if (product_id.isEmpty()) {
            throw new RuntimeException(String.format("Beer product does not exist"));
        } else {
            // TODO schöner machen

            var query_bottle_id = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--select-by-productID.sql");

            var bottling_ids = jdbi.withHandle(handle -> handle
                    .createQuery(query_bottle_id)
                    .bind("product_id", product_id)
                    .map((rs, ctx) -> {
                        return rs.getInt("id");
                    })
                    .collect(Collectors.toList()));

            var remaining_bottles = bottles;
            var query_update_bottling = Templates.renderTemplateFromResources("db/sql.sales/beers--bottlings--updateByProductName.sql");
            for(int i = 0; i < bottling_ids.size(); i++){
                var current_id = bottling_ids.get(i).intValue();
                // get number of bottles in this bottling:
                var bottle_count = readBottling(current_id).get().getBottles();

                var count_removed_bottles = Math.min(bottle_count,remaining_bottles);
                remaining_bottles = remaining_bottles - count_removed_bottles;

                jdbi.withHandle(handle -> handle
                        .createUpdate(query_update_bottling)
                        .bind("bottles", bottle_count-count_removed_bottles)
                        .bind("id", current_id)
                        .execute());
            }

        }
    }


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
