package systems.brewery;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.brewery.values.Ingredient;
import systems.brewery.values.Recipe;
import systems.brewery.values.instructions.*;
import systems.reference.model.Employee;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class RecipesJdbcImpl implements Recipes {

    private final Jdbi jdbi;

    private final Ingredients ingredients;

    @Override
    public void insertRecipe(Recipe recipe) {
        var query = Templates.renderTemplateFromResources("sql/brewery/recipes--insert.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("beer_id", recipe.getBeerKey())
            .bind("beer_name", recipe.getBeerName())
            .bind("product_owner", recipe.getProductOwner().getId())
            .bind("created", Instant.now())
            .bind("updated", Instant.now())
            .execute());

        for (int i = 0; i < recipe.getInstructions().size(); i++) {
            this.insertInstruction(recipe, recipe.getInstructions().get(i), i + 1);
        }
    }

    @Override
    public Optional<Recipe> findRecipeByName(String beerKey) {
        var query = Templates.renderTemplateFromResources("sql/brewery/recipes--select-by-beer-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("beer_id", beerKey)
            .map((rs, ctx) -> {
                var productOwner = Employee.apply(
                    rs.getString("e_id"), rs.getString("e_firstname"), rs.getString("e_name"),
                    rs.getTimestamp("e_date_of_birth").toInstant(), rs.getString("e_position"));

                var instructions = readInstructions(beerKey);

                return Recipe.apply(
                    rs.getString("beer_id"), rs.getString("beer_name"), productOwner,
                    rs.getTimestamp("created").toInstant(), rs.getTimestamp("updated").toInstant(),
                    instructions);
            })
            .findFirst());
    }

    @Override
    public void removeRecipe(String beerKey) {
        var query = Templates.renderTemplateFromResources("sql/brewery/recipes--delete.sql");
        removeInstructions(beerKey);

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("beer_key", beerKey)
            .execute());
    }

    @Override
    public void clear() {
        clearInstructions();
        var query = Templates.renderTemplateFromResources("sql/brewery/recipes--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearInstructions() {
        clearBoilings();
        clearRested();
        clearSparged();
        clearMashings();
        clearIngredientAdds();

        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearBoilings() {
        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--boilings--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearIngredientAdds() {
        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--ingredient-adds--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearMashings() {
        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--mashings--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearRested() {
        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--mashing-rests--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearSparged() {
        var query = Templates.renderTemplateFromResources("sql/brewery/brews--sparged--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void insertInstruction(Recipe recipe, Instruction instruction, int sort) {
        var insertInstructionQuery = Templates.renderTemplateFromResources("sql/brewery/instructions--insert.sql");
        var selectInstructionQuery = Templates.renderTemplateFromResources("sql/brewery/instructions--select-id.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(insertInstructionQuery)
            .bind("beer_key", recipe.getBeerKey())
            .bind("sort", sort)
            .execute());

        var instructionId = jdbi.withHandle(handle -> handle
            .createQuery(selectInstructionQuery)
            .bind("beer_key", recipe.getBeerKey())
            .bind("sort", sort)
            .map((rs, ctx) -> rs.getInt("id"))
            .first());

        if (instruction instanceof AddIngredient) {
            var inst = (AddIngredient) instruction;
            var query = Templates.renderTemplateFromResources("sql/brewery/instructions--ingredient-adds--insert.sql");
            var ingredientId = ingredients.getIngredientIdByName(inst.getIngredient().getName());

            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id", instructionId)
                .bind("ingredient", ingredientId)
                .bind("amount", inst.getAmount())
                .execute());
        } else if (instruction instanceof Boil) {
            var inst = (Boil) instruction;
            var query = Templates.renderTemplateFromResources("sql/brewery/instructions--boilings--insert.sql");

            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id", instructionId)
                .bind("duration", inst.getDuration().get(ChronoUnit.MINUTES))
                .execute());
        } else if (instruction instanceof Mash) {
            var inst = (Mash) instruction;
            var query = Templates.renderTemplateFromResources("sql/brewery/instructions--mashings--insert.sql");

            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id", instructionId)
                .bind("start_temperature", inst.getStartTemperature())
                .bind("end_temperature", inst.getEndTemperature())
                .bind("duration", inst.getDuration().get(ChronoUnit.MINUTES))
                .execute());
        } else if (instruction instanceof Rest) {
            var inst = (Rest) instruction;
            var query = Templates.renderTemplateFromResources("sql/brewery/instructions--mashing-rests--insert.sql");

            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id", instructionId)
                .bind("duration", inst.getDuration().get(ChronoUnit.MINUTES))
                .execute());
        } else if (instruction instanceof Sparge) {
            var inst = (Sparge) instruction;
            var query = Templates.renderTemplateFromResources("sql/brewery/instructions--sparge--insert.sql");

            jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("id", instructionId)
                .bind("duration", inst.getDuration().get(ChronoUnit.MINUTES))
                .execute());
        } else {
            throw new RuntimeException(String.format("Unknown instruction type `%s`.", instruction.getClass()
                .getName()));
        }
    }

    private List<Instruction> readInstructions(String beerKey) {
        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--select-by-recipe.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("beer_key", beerKey)
            .map((rs, ctx) -> rs.getInt("id"))
            .stream()
            .map(this::readInstruction)
            .collect(Collectors.toList()));
    }

    private Instruction readInstruction(int instructionId) {
        Optional<? extends Instruction> result;
        result = readInstructionAsAddIngredient(instructionId);

        if (result.isEmpty()) result = readInstructionAsBoil(instructionId);
        if (result.isEmpty()) result = readInstructionAsMashingRest(instructionId);
        if (result.isEmpty()) result = readInstructionAsMash(instructionId);
        if (result.isEmpty()) result = readInstructionAsSparge(instructionId);

        return result.orElseThrow(() -> new RuntimeException(
            String.format("Could not find known instruction type for instruction `%d`", instructionId)));
    }

    private Optional<AddIngredient> readInstructionAsAddIngredient(int instructionId) {
        var selectInstruction = Templates.renderTemplateFromResources("sql/brewery/instructions--ingredient-adds" +
            "--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(selectInstruction)
            .bind("id", instructionId)
            .map((rs, ctx) -> AddIngredient.apply(Ingredient.apply(rs.getString("ingredient"), rs.getString("unit")), rs
                .getDouble("amount")))
            .findFirst());
    }

    private Optional<Boil> readInstructionAsBoil(int instructionId) {
        var selectInstruction = Templates.renderTemplateFromResources("sql/brewery/instructions--boilings--select-by" +
            "-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(selectInstruction)
            .bind("id", instructionId)
            .map((rs, ctx) -> Boil.apply(Duration.ofMinutes(rs.getLong("duration"))))
            .findFirst());
    }

    private Optional<Rest> readInstructionAsMashingRest(int instructionId) {
        var selectInstruction = Templates.renderTemplateFromResources("sql/brewery/instructions--mashing-rests" +
            "--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(selectInstruction)
            .bind("id", instructionId)
            .map((rs, ctx) -> Rest.apply(Duration.ofMinutes(rs.getLong("duration"))))
            .findFirst());
    }

    private Optional<Mash> readInstructionAsMash(int instructionId) {
        var selectInstruction = Templates.renderTemplateFromResources("sql/brewery/instructions--mashings--select-by" +
            "-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(selectInstruction)
            .bind("id", instructionId)
            .map((rs, ctx) -> Mash.apply(rs.getDouble("start_temperature"), rs.getDouble("end_temperature"),
                Duration.ofMinutes(rs
                    .getLong("duration"))))
            .findFirst());
    }

    private Optional<Sparge> readInstructionAsSparge(int instructionId) {
        var selectInstruction = Templates.renderTemplateFromResources("sql/brewery/instructions--sparge--select-by" +
            "-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(selectInstruction)
            .bind("id", instructionId)
            .map((rs, ctx) -> Sparge.apply(Duration.ofMinutes(rs.getLong("duration"))))
            .findFirst());
    }

    private void removeInstructions(String beerKey) {
        var query = Templates.renderTemplateFromResources("sql/brewery/instructions--select-ids.sql");

        jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("beer_key", beerKey)
            .map((rs, ctx) -> rs.getInt("id"))
        ).forEach(id -> {
            removeInstruction(id, "instructions--boilings--delete.sql");
            removeInstruction(id, "instructions--ingredient-adds--delete.sql");
            removeInstruction(id, "instructions--mashing-rests--delete.sql");
            removeInstruction(id, "instructions--mashings--delete.sql");
            removeInstruction(id, "instructions--sparge--delete.sql");

            var deleteQuery = Templates.renderTemplateFromResources("sql/brewery/instructions--select-ids.sql");

            jdbi.withHandle(handle -> handle
                .createUpdate(deleteQuery)
                .bind("beer_key", beerKey)
                .execute());
        });
    }

    private void removeInstruction(int id, String template) {
        var query = Templates.renderTemplateFromResources("sql/brewery/" + template);

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", id)
            .execute());
    }

}
