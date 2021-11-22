package systems.brewery.ports;

import common.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;
import systems.brewery.values.RecipeProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class BreweryRepositoryJdbcImpl implements BreweryRepositoryPort {

    private final Jdbi jdbi;

    public static BreweryRepositoryJdbcImpl apply(DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        return apply(jdbi);
    }

    @Override
    public void insertRecipe(RecipeProperties recipe) {
        var query = "INSERT INTO sppl.PROD_RECIPES " +
            "(beer_id, beer_name, product_owner, created, updated) VALUES " +
            "(:beer_id, :beer_name, :product_owner, :created, :updated)";

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("beer_id", recipe.getBeerId())
            .bind("beer_name", recipe.getBeerName())
            .bind("product_owner", recipe.getProductOwner())
            .bind("created", recipe.getCreated())
            .bind("updated", recipe.getUpdated())
            .execute());
    }

    @Override
    public Optional<RecipeProperties> findRecipeByName(String beerKey) {
        // Rename because it is based on key instead string?
        var query = "SELECT * FROM sppl.PROD_RECIPES WHERE beer_id = :beer_id";

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("beer_id", beerKey)
                .map(new RecipeMapper())
                .stream()
                .findFirst());
    }

    @Override
    public void updateRecipe(RecipeProperties recipe) {
        var query = "UPDATE sppl.PROD_RECIPES " +
                "SET beer_id = :beer_id, beer_name = :beer_name, product_owner = :product_owner, created = :created, " +
                "updated = :updated" + "WHERE id = :id";

        //TODO: Fehlerkorrektur
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("beer_id", recipe.getBeerId())
                .bind("beer_name", recipe.getBeerName())
                .bind("product_owner", recipe.getProductOwner())
                .bind("created", recipe.getCreated())
                .bind("updated", recipe.getUpdated())
                .execute());
    }

    @Override
    public void removeRecipe(String beerName) {
        var query = "DELETE FROM sppl.PROD_RECIPES WHERE beer_name = :beer_name";
        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("beer_name", beerName)
                .execute());
    }

    private static class RecipeMapper implements RowMapper<RecipeProperties> {

        @Override
        public RecipeProperties map(ResultSet rs, StatementContext ctx) throws SQLException {
            //return Ingredient.apply(rs.getString("name"), rs.getString("unit"));
            return RecipeProperties.apply(rs.getString("beer_id"), rs.getString("beer_name"),
                    rs.getString("product_owner"), rs.getTimestamp("created").toInstant(),
                    rs.getTimestamp("updated").toInstant());
        }

    }


    @Override
    public void insertIngredient(Ingredient ingredient) {
        var query = "INSERT INTO sppl.PROD_INGREDIENTS (name, unit) VALUES (:name, :unit)";

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("name", ingredient.getName())
            .bind("unit", ingredient.getUnit())
            .execute());
    }

    @Override
    public void removeIngredient(String name) {
        var query = "DELETE FROM sppl.PROD_INGREDIENTS WHERE name = :name";

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("name", name)
            .execute());
    }

    @Override
    public Optional<Ingredient> getIngredientByName(String name) {
        var query = "SELECT * FROM sppl.PROD_INGREDIENTS WHERE name = :name";

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("name", name)
            .map(new IngredientMapper())
            .stream()
            .findFirst());
    }

    private static class IngredientMapper implements RowMapper<Ingredient> {

        @Override
        public Ingredient map(ResultSet rs, StatementContext ctx) throws SQLException {
            return Ingredient.apply(rs.getString("name"), rs.getString("unit"));
        }

    }

    @Override
    public void insertIngredientProduct(IngredientProduct ingredientProduct){
        var query = "INSERT INTO sppl.PROD_INGREDIENT_PRODUCTS (ingredient_id, producer_product_id, producer_name, " +
                "product_name)" + "VALUES (:ingredient_id, :producer_product_id, :producer_name, :product_name)";

        //Alernative solution: add a variable "id" to the class ingredient (performance?)
        var ing_id = jdbi.withHandle(handle -> handle
                .createQuery("SELECT id FROM sppl.PROD_INGREDIENTS WHERE name = :name")
                .bind("name", ingredientProduct.getIngredient().getName())
                .mapTo(Integer.class)
                .stream()
                .findFirst()
                .get());

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("ingredient_id", ing_id)
                .bind("producer_product_id", ingredientProduct.getProducerProductId())
                .bind("producer_name", ingredientProduct.getProducerName())
                .bind("product_name", ingredientProduct.getProductName())
                .execute());
    }

    @Override
    public Optional<IngredientProduct> getIngredientProductByName(String producerName, String productName){
        var query = "SELECT * FROM sppl.PROD_INGREDIENT_PRODUCTS AS ip JOIN sppl.PROD_INGREDIENTS AS i " +
                "ON ip.ingredient_id = i.id WHERE ip.producer_name = :producer_name AND ip.product_name = :product_name";

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("producer_name", producerName)
                .bind("product_name", productName)
                .map(new IngredientProductMapper())
                .stream()
                .findFirst());
    }

    @Override
    public List<IngredientProduct> findIngredientProductByIngredientName(String IngredientName){
        var query = "SELECT * FROM sppl.PROD_INGREDIENTS AS i JOIN sppl.PROD_INGREDIENT_PRODUCTS AS ip " +
                "ON ip.ingredient_id = i.id WHERE i.name = :name";

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("name", IngredientName)
                .map(new IngredientProductMapper())
                .list());
    }

    @Override
    public List<IngredientProduct> selectAllIngredientProducts(){
        var query = "SELECT * FROM sppl.PROD_INGREDIENTS AS i JOIN sppl.PROD_INGREDIENT_PRODUCTS AS ip ON ip.ingredient_id = i.id";

        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .map(new IngredientProductMapper())
                .list());
    }

    // Not debugged yet
    @Override
    public void updateIngredientProduct(IngredientProduct ingredientProduct) {
        var query = "UPDATE sppl.PROD_INGREDIENT_PRODUCTS " +
                "SET ingredient_id = :ingredient_id, producer_product_id = :producer_product_id, producer_name = :producer_name, product_name = :product_name " +
                "WHERE producer_product_id = :producer_product_id OR (producer_name = :producer_name AND product_name = :product_name)";

        var ing_id = jdbi.withHandle(handle -> handle
                .createQuery("SELECT id FROM sppl.PROD_INGREDIENTS WHERE name = :name")
                .bind("name", ingredientProduct.getIngredient().getName())
                .mapTo(Integer.class)
                .stream()
                .findFirst()
                .get());

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("ingredient_id", ing_id)
                .bind("producer_product_id", ingredientProduct.getProducerProductId())
                .bind("producer_name", ingredientProduct.getProducerName())
                .bind("product_name", ingredientProduct.getProductName())
                .execute());
    }

    // Not debugged yet
    @Override
    public void removeIngredientProduct(String productName, String producerName) {
        var query = "DELETE FROM sppl.PROD_INGREDIENT_PRODUCTS WHERE (producer_name = :producer_name AND product_name = :product_name)";

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("producer_name", producerName)
                .bind("product_name", productName)
                .execute());

    }

    public static class IngredientProductMapper implements RowMapper<IngredientProduct> {

        @Override
        public IngredientProduct map(ResultSet rs, StatementContext ctx) throws SQLException {
            return IngredientProduct.apply(
                    Ingredient.apply(rs.getString("name"), rs.getString("unit")),
                    rs.getString("producer_product_id"),
                    rs.getString("producer_name"),
                    rs.getString("product_name")
            );
        }

    }

}
