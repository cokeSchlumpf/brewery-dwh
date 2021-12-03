package systems.brewery;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.brewery.values.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class IngredientsJdbcImpl implements Ingredients {

    private final Jdbi jdbi;

    @Override
    public void insertIngredient(Ingredient ingredient) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredients--insert.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("name", ingredient.getName())
            .bind("unit", ingredient.getUnit())
            .execute());
    }

    @Override
    public Optional<Ingredient> getIngredientByName(String name) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredients--select-by-name.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("name", name)
            .map(IngredientMapper.apply())
            .findFirst());
    }

    @Override
    public int getIngredientIdByName(String name) {
        return findIngredientIdByName(name).orElseThrow(() ->
            new RuntimeException(String.format("Ingredient not found `%s`", name)));
    }

    @Override
    public void insertOrUpdateIngredient(Ingredient ingredient) {
        var existingId = findIngredientIdByName(ingredient.getName());

        if (existingId.isEmpty()) {
            insertIngredient(ingredient);
        } else {
            var query = Templates.renderTemplateFromResources("sql/brewery/ingredients--update.sql");

            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("name", ingredient.getName())
                .bind("unit", ingredient.getUnit())
                .bind("id", existingId)
                .execute());
        }
    }

    @Override
    public void removeIngredient(String ingredient) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredients--delete.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("name", ingredient)
            .execute());
    }

    @Override
    public void clear() {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredients--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private Optional<Integer> findIngredientIdByName(String ingredientName) {
        var query = Templates.renderTemplateFromResources("sql/brewery/ingredients--select-id-by-name.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("name", ingredientName)
            .mapTo(Integer.class)
            .findOne());
    }

    @AllArgsConstructor(staticName = "apply")
    private static class IngredientMapper implements RowMapper<Ingredient> {

        @Override
        public Ingredient map(ResultSet rs, StatementContext ctx) throws SQLException {
            return Ingredient.apply(rs.getString("name"), rs.getString("unit"));
        }

    }

}
