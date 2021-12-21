package simulation;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.entities.brewery.Brewery;
import simulation.entities.customer.Customer;
import simulation.entities.customer.behaviors.CustomerBehaviors;
import simulation.entities.employee.Employee;
import simulation.entities.onlinestore.OnlineStore;
import systems.brewery.BreweryManagementSystem;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;
import systems.brewery.values.Recipe;
import systems.reference.ReferenceDataManagement;
import systems.sales.SalesManagementSystem;
import systems.sales.values.Product;

public final class World extends AbstractBehavior<World.WorldMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(World.class);

    interface WorldMessage {
    }

    private World(ActorContext<WorldMessage> context) {
        super(context);
    }

    public static Behavior<WorldMessage> create(ReferenceDataManagement refDataManagement,
                                                BreweryManagementSystem bms, Brewery brewery,
                                                SalesManagementSystem sms) {
        /*
         * Clear database.
         */
        bms.getBrews().clear();
        bms.getRecipes().clear();
        bms.getIngredientProducts().clear();
        bms.getIngredients().clear();
        sms.getOrders().clear();
        sms.getProducts().clear();
        sms.getCustomers().clear();
        LOG.info("Cleaned database.");

        /*
         * Configure initial data
         */
        systems.reference.model.Employee.predefined().forEach(refDataManagement::registerOrUpdateEmployee);
        Ingredient.predefined().forEach(ingredient -> bms.getIngredients().insertIngredient(ingredient));
        IngredientProduct.predefined().forEach(product -> bms.getIngredientProducts().insertIngredientProduct(product));
        Recipe.predefined().forEach(recipe -> bms.getRecipes().insertRecipe(recipe));
        Product.predefined().forEach(product -> sms.getProducts().insertProduct(product));
        LOG.info("Initial data inserted.");

        /*
         *
         */
        return Behaviors.setup(ctx -> {
            var store = ctx.spawn(OnlineStore.create(sms), "store");

            ctx.spawn(Employee.create(systems.reference.model.Employee.johnny(), store, bms, sms, brewery), "johnny");
            ctx.spawn(Customer.create(store, CustomerBehaviors.createSam()), "sam");
            ctx.spawn(Customer.create(store, CustomerBehaviors.createOlga()), "olga");
            return new World(ctx);
        });
    }

    @Override
    public Receive<WorldMessage> createReceive() {
        return newReceiveBuilder()
            .build();
    }

}
