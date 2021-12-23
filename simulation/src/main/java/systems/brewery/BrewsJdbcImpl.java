package systems.brewery;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.reference.model.Employee;
import systems.sales.values.Bottling;
import systems.brewery.values.Brew;
import systems.brewery.values.event.*;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class BrewsJdbcImpl implements Brews {

    private final Jdbi jdbi;

    private final IngredientProducts ingredientProducts;

    private final Recipes recipes;

    @Override
    public void insertBrew(Brew brew) {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--insert.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("beer_key", brew.getBeer().getBeerKey())
            .bind("brewer", brew.getBrewer().getId())
            .bind("brew_start", brew.getStart())
            .bind("original_gravity", brew.getOriginalGravity())
            .execute());
    }

    @Override
    public void updateBrew(Instant finished, double finalGravity) {
        var currentBrewId = getLatestBrewId();
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--update.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", currentBrewId)
            .bind("brew_end", finished)
            .bind("final_gravity", finalGravity)
            .execute());
    }

    @Override
    public void logBrewEvent(BrewEvent event) {
        var currentBrewId = getLatestBrewId();

        if (event instanceof Boiled) {
            logBoiled(currentBrewId, (Boiled) event);
        } else if (event instanceof  IngredientAdded) {
            logIngredientAdded(currentBrewId, (IngredientAdded) event);
        } else if (event instanceof  Mashed) {
            logMashed(currentBrewId, (Mashed) event);
        } else if (event instanceof Rested) {
            logRested(currentBrewId, (Rested) event);
        } else if (event instanceof  Sparged) {
            logSparged(currentBrewId, (Sparged) event);
        } else {
            throw new RuntimeException(String.format("Unknown brew event `%s`", event.getClass()));
        }
    }

    public List<Brew> getBrewsByBeerId(String beer_id){

        //ToDo: List of Brew events implementieren

        // Recipe
        var recipe = recipes.findRecipeByName(beer_id);

        // get all Brews
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--brew-select-by-beerID.sql");

        var brews = jdbi.withHandle(handle -> handle
                                                        .createQuery(query)
                                                        .bind("beer", beer_id)
                                                        .map((rs,ctx) -> {
                                                            var employee = Employee.apply(
                                                                    rs.getString("e_id"), rs.getString("e_firstname"), rs.getString("e_name"),
                                                                    rs.getTimestamp("e_date_of_birth").toInstant(), rs.getString("e_position"));
                                                            var start = rs.getTimestamp("brew_start").toInstant();
                                                            var end = rs.getTimestamp("brew_start").toInstant();
                                                            var brew = Brew.apply(recipe.get(), employee,start,end, rs.getDouble("original_gravity"),rs.getDouble("final_gravity"), List.of());
                                                            return brew;
                                                        })
                                                        .list());


        return brews;
    }

    @Override
    public void clear() {
        clearBoiled();
        clearMashed();
        clearRested();
        clearIngredientAdded();
        clearSparged();

        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearBoiled() {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--boilings--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearIngredientAdded() {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--ingredient-added--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearMashed() {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--mashed--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearRested() {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--rested--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private void clearSparged() {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--sparged--delete-all.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    private List<Sparged> findSparged(int brewId){
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--sparged-select-by-brewId.sql");
        var spargings = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("brew_id", brewId)
                .map((rs,ctx) -> {
                    return Sparged.apply(rs.getTimestamp("start_time").toInstant(), rs.getTimestamp("end_time").toInstant());
                })
                .list());
        return spargings;
    }

    private List<Boiled> findBoiled(int brewId){
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--boilings-select-by-brewId.sql");
        var boilings = jdbi.withHandle(handle -> handle
                .createQuery(query)
                .bind("brew_id", brewId)
                .map((rs,ctx) -> {
                    return Boiled.apply(rs.getTimestamp("start_time").toInstant(), rs.getTimestamp("end_time").toInstant());
                })
                .list());
        return boilings;
    }

    private void logSparged(int brewId, Sparged sparged) {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--sparged--insert.sql");

        jdbi.withHandle(handle -> handle
                .createUpdate(query)
                .bind("brew_id", brewId)
                .bind("start_time", sparged.getStart())
                .bind("end_time", sparged.getEnd())
                .execute());
    }

    private void logBoiled(int brewId, Boiled boiled) {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--boilings--insert.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("brew_id", brewId)
            .bind("start_time", boiled.getStart())
            .bind("end_time", boiled.getEnd())
            .execute());
    }

    private void logIngredientAdded(int brewId, IngredientAdded ingredientAdded) {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--ingredient-added--insert.sql");

        var ingredientProductIdByName = ingredientProducts.getIngredientProductIdByName(
            ingredientAdded.getProduct().getProducerName(),
            ingredientAdded.getProduct().getProductName());

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("brew_id", brewId)
            .bind("moment", ingredientAdded.getMoment())
            .bind("ingredient_product", ingredientProductIdByName)
            .bind("amount", ingredientAdded.getAmount())
            .execute());
    }

    private void logMashed(int brewId, Mashed mashed) {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--mashed--insert.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("brew_id", brewId)
            .bind("start_time", mashed.getStart())
            .bind("end_time", mashed.getEnd())
            .bind("start_temperature", mashed.getStartTemperature())
            .bind("end_temperature", mashed.getEndTemperature())
            .execute());
    }

    private void logRested(int brewId, Rested rested) {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--rested--insert.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("brew_id", brewId)
            .bind("start_time", rested.getStart())
            .bind("end_time", rested.getEnd())
            .execute());
    }

    private Integer getLatestBrewId() {
        var query = Templates.renderTemplateFromResources("db/sql/brewery/brews--select-by-start.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map((rs, ctx) -> rs.getInt("id"))
            .first());
    }

}
