package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.sales.values.Bottling;
import systems.sales.values.Product;
import systems.sales.values.StockProduct;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class StockProductsJdbcImpl implements StockProducts {

    private final Jdbi jdbi;

    @Override
    public void addToStock(Product product, int count) {
        var stockproduct = findStockByProduct(product);
        var product_id = findProductIdByName(product.getProductName()).orElseThrow();

        if (stockproduct.isEmpty()) {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--insert.sql");
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("product_id", product_id)
                .bind("bottles", count)
                .bind("reserved", 0)
                .execute());
        } else {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--update.sql");
            var new_bottle_count = stockproduct.get().getAmount();
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("bottles", new_bottle_count)
                .bind("reserved", stockproduct.get().getReserved())
                .bind("product_id", product_id)
                .execute());
        }

    }

    @Override
    public void insertBeerProduct(Product product) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--insert.sql");
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
        var product_id = findProductIdByName(bottling.getProduct().getProductName());

        if (product_id.isEmpty()) {
            throw new RuntimeException("Product with product_id " + product_id + "does not exist in system.");
        } else {
            var query = Templates.renderTemplateFromResources("db/sql/sales/bottlings--insert.sql");
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("product_id", product_id)
                .bind("bottled", bottling.getBottled())
                .bind("best_before_date", bottling.getBestBefore())
                .bind("bottles", bottling.getBottles())
                .execute());
        }
    }

    private Optional<StockProduct> findStockByProduct(Product product) {

        var product_id = findProductIdByName(product.getProductName());

        if (product_id.isPresent()) {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--select-by-product.sql");

            return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("product_id", product_id)
                .map((rs, ctx) -> StockProduct.apply(product, rs.getInt("bottles"), rs.getInt("reserved")))
                .findFirst());
        } else {
            throw new RuntimeException("Product does not exist");
        }
    }

    private Optional<Integer> findProductIdByName(String product_name) {

        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-id-by-name.sql");
        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("product_name", product_name)
            .mapTo(Integer.class)
            .findOne());
    }


    @Override
    public void removeFromStock(Product product, int count) {
        var stockproduct = findStockByProduct(product);
        var product_id = findProductIdByName(product.getProductName());

        if (stockproduct.isPresent()) {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--update.sql");
            var new_bottle_count = stockproduct.get().getAmount() - count;
            var new_reserved_count = stockproduct.get().getReserved() - count;
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("bottles", new_bottle_count)
                .bind("reserved", new_reserved_count)
                .bind("product_id", product_id)
                .execute());
        } else {
            throw new RuntimeException("Product does not exist in stock");
        }

    }

    @Override
    public void reserveOnStock(Product product, int count) {
        var stockproduct = findStockByProduct(product);
        var product_id = findProductIdByName(product.getProductName());

        if (stockproduct.isPresent()) {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--update.sql");
            var new_reserved_count = stockproduct.get().getReserved() + count;
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("bottles", stockproduct.get().getAmount())
                .bind("reserved", new_reserved_count)
                .bind("product_id", product_id)
                .execute());
        } else {
            throw new RuntimeException("Product does not exist in stock");
        }
    }

    @Override
    public Optional<Integer> findProductId(Product product) {
        return findProductIdByName(product.getProductName());
    }

    @Override
    public Optional<Product> findProductById(int id) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("product_id", id)
            .map(ProductMapper.apply())
            .findFirst());
    }

    @Override
    public List<Product> findBeerProductsByBeerId(String beerId) {

        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-by-beer-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("beer_id", beerId)
            .map(ProductMapper.apply())
            .list());

    }

    private Optional<Product> readBeerProductByName(String product_name) {

        var existingId = findProductIdByName(product_name);
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("product_id", existingId)
            .map(ProductMapper.apply())
            .findFirst());
    }

    @Override
    public List<Product> listAllBeerProducts() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-all.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map(ProductMapper.apply())
            .list());
    }

    @Override
    public Optional<StockProduct> findAvailableProductByProductName(String productName) {
        return Optional.empty();
    }

    @Override
    public List<StockProduct> listAvailableProducts() {
        return null;
    }


    @Override
    public void clear() {
        this.clearBottlings();
        this.clearStockProducts();
        this.clearProducts();
    }

    private void clearProducts() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--delete-all.sql");
        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearBottlings() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/bottlings--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearStockProducts() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    @AllArgsConstructor(staticName = "apply")
    private static class ProductMapper implements RowMapper<Product> {

        @Override
        public Product map(ResultSet rs, StatementContext ctx) throws SQLException {
            return Product.apply(rs.getString("product_name"), rs.getString("beer_id"), rs.getDouble("price"),
                rs.getDouble("volume"));
        }

    }

}
