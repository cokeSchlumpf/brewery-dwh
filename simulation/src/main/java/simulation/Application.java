package simulation;

import common.DatabaseConfiguration;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import systems.brewery.IngredientProducts;
import systems.brewery.ports.BreweryRepositoryJdbcImpl;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;
import systems.reference.ReferenceDataManagement;
import systems.reference.model.Employee;
import systems.reference.ports.ReferenceDataRepositoryJdbcImpl;

import java.time.Instant;
import java.util.List;

public final class Application {

    private static Logger LOG = LoggerFactory.getLogger("sim");

    public static void main(String... args) {
        var johnny = Employee.apply("johnny", "Johnny", "Goldgreen", Instant.now(), "Founder 1");
        var mike = Employee.apply("mike", "Mike", "Hemington", Instant.now(), "Founder");

        var databaseConfig = DatabaseConfiguration.apply("jdbc:postgresql://localhost:5432/brewery", "postgres", "password");
        var refDataRepo = ReferenceDataRepositoryJdbcImpl.apply(databaseConfig);
        var refDataMgmt = ReferenceDataManagement.apply(refDataRepo);

        refDataMgmt.registerOrUpdateEmployee(johnny);
        refDataMgmt.registerOrUpdateEmployee(mike);

        System.out.println(refDataMgmt.findEmployeeById("johnny"));

        // Create ingredientproducts and ingredients
        var repository = BreweryRepositoryJdbcImpl.apply(databaseConfig);
        var ingredientproducts = IngredientProducts.apply(repository);
        var coffee = Ingredient.apply("coffee", "g");
        var coke = Ingredient.apply("coke", "l");
        repository.insertIngredient(coffee);
        repository.insertIngredient(coke);

        IngredientProduct ingredientProduct = IngredientProduct.apply(coffee, "122", "Mokka", "Fix");
        repository.insertIngredientProduct(ingredientProduct);

        ingredientproducts.registerOrUpdate(IngredientProduct.apply(coffee, "123", "Tchibo", "Kaffee"));
        ingredientproducts.registerOrUpdate(IngredientProduct.apply(coffee, "122", "Mokka", "Fix"));
        ingredientproducts.registerOrUpdate(IngredientProduct.apply(coke, "121", "Pepsi", "Cola"));
        ingredientproducts.registerOrUpdate(IngredientProduct.apply(coke, "123", "Coca", "Cola"));
        ingredientproducts.registerOrUpdate(IngredientProduct.apply(coke, "123", "Vita", "Cola"));

        //ingredientproducts.remove("Fix", "Mokka");
    }

}
