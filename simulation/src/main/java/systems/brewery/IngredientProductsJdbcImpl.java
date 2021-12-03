package systems.brewery;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class IngredientProductsJdbcImpl implements IngredientProducts {

    private final Jdbi jdbi;

    private final Ingredients ingredients;

    @Override
    public void insertIngredientProduct(IngredientProduct ingredientProduct) {
        ingredients.insertOrUpdateIngredient(ingredientProduct.getIngredient());

        var ingredientId = ingredients.getIngredientIdByName(ingredientProduct.getIngredient().getName());
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--insert");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("ingredient_id", ingredientId)
            .bind("producer_product_id", ingredientProduct.getProducerProductId())
            .bind("producer_name", ingredientProduct.getProducerName())
            .bind("product_name", ingredientProduct.getProductName())
            .execute());
    }

    @Override
    public List<IngredientProduct> selectAllIngredientProducts() {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--select.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map(IngredientProductMapper.apply())
            .collect(Collectors.toList()));
    }

    @Override
    public Optional<IngredientProduct> getIngredientProductByName(String producerName, String productName) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--select-by-producer-and" +
            "-product.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("producer_name", producerName)
            .bind("product_name", productName)
            .map(IngredientProductMapper.apply())
            .findFirst());
    }

    @Override
    public int getIngredientProductIdByName(String producerName, String productName) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--select-by-producer-and" +
            "-product.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("producer_name", producerName)
            .bind("product_name", productName)
            .map((rs, ctx) -> rs.getInt("id"))
            .first());
    }

    @Override
    public List<IngredientProduct> findIngredientProductByIngredientName(String ingredientName) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--select-by-ingredient.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("ingredient_name", ingredientName)
            .map(IngredientProductMapper.apply())
            .collect(Collectors.toList()));
    }

    @Override
    public void removeIngredientProduct(String productName, String producerName) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--delete.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("product_name", producerName)
            .bind("producer_name", producerName)
            .execute());
    }

    @Override
    public void clear() {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredient-products--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    @AllArgsConstructor(staticName = "apply")
    private static class IngredientProductMapper implements RowMapper<IngredientProduct> {

        @Override
        public IngredientProduct map(ResultSet rs, StatementContext ctx) throws SQLException {
            return IngredientProduct.apply(
                Ingredient.apply(rs.getString("ingredient_name"), rs.getString("ingredient_unit")),
                rs.getString("producer_product_id"),
                rs.getString("producer_name"),
                rs.getString("product_name")
            );
        }

    }

}
