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
        var stockProduct = findStockProductByName(product.getProductName());
        var productId = findProductId(product.getProductName()).orElseThrow();

        if (stockProduct.isEmpty()) {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--insert.sql");
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("product_id", productId)
                .bind("bottles", count)
                .bind("reserved", 0)
                .execute());
        } else {
            var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--update.sql");
            var newBottleCount = stockProduct.get().getAmount() + count;
            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("bottles", newBottleCount)
                .bind("reserved", stockProduct.get().getReserved())
                .bind("product_id", productId)
                .execute());
        }

    }

    @Override
    public void insertProduct(Product product) {
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
        var productId = getProductId(bottling.getProduct().getProductName());

        var query = Templates.renderTemplateFromResources("db/sql/sales/bottlings--insert.sql");
        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("product_id", productId)
            .bind("bottled", bottling.getBottled())
            .bind("best_before_date", bottling.getBestBefore())
            .bind("bottles", bottling.getBottles())
            .execute());
    }


    @Override
    public void removeFromStock(Product product, int count) {
        var stockProduct = getStockProductByName(product.getProductName());
        var productId = findProductId(product.getProductName());

        var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--update.sql");
        var newBottleCount = stockProduct.getAmount() - count;
        var newReservedCount = stockProduct.getReserved() - count;

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("bottles", newBottleCount)
            .bind("reserved", newReservedCount)
            .bind("product_id", productId)
            .execute());
    }

    @Override
    public void reserveOnStock(Product product, int count) {
        var stockProduct = getStockProductByName(product.getProductName());
        var productId = findProductId(product.getProductName());

        var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--update.sql");
        var new_reserved_count = stockProduct.getReserved() + count;

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("bottles", stockProduct.getAmount())
            .bind("reserved", new_reserved_count)
            .bind("product_id", productId)
            .execute());
    }

    @Override
    public Optional<Integer> findProductId(String productName) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-id-by-name.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("product_name", productName)
            .mapTo(Integer.class)
            .findOne());
    }

    @Override
    public Optional<Integer> findProductId(Product product) {
        return findProductId(product.getProductName());
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
    public Optional<Product> findProductByName(String name) {
        var existingId = findProductId(name);
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("product_id", existingId)
            .map(ProductMapper.apply())
            .findFirst());
    }

    @Override
    public List<Product> getProductsByBeerId(String beerId) {

        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-by-beer-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("beer_id", beerId)
            .map(ProductMapper.apply())
            .list());

    }

    @Override
    public List<Product> getAllProducts() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/products--select-all.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map(ProductMapper.apply())
            .list());
    }

    @Override
    public Optional<StockProduct> findStockProductByName(String productName) {
        var productId = getProductId(productName);

        var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--select-by-product.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("product_id", productId)
            .map(StockProductMapper.apply())
            .findFirst());
    }

    @Override
    public List<StockProduct> getAllStockProducts() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/stock-product--select.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map(StockProductMapper.apply())
            .list());
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
    private static class StockProductMapper implements RowMapper<StockProduct> {

        @Override
        public StockProduct map(ResultSet rs, StatementContext ctx) throws SQLException {
            var product = ProductMapper.apply().map(rs, ctx);
            return StockProduct.apply(product, rs.getInt("bottles"), rs.getInt("reserved"));
        }
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
