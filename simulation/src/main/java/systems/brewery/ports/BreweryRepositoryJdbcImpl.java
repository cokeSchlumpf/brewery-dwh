package systems.brewery.ports;

import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.brewery.values.Ingredient;
import systems.brewery.values.RecipeProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    }

    @Override
    public Optional<RecipeProperties> findRecipeByName(String beerKey) {
        return Optional.empty();
    }

    @Override
    public void removeRecipe(String beerName) {

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

}
