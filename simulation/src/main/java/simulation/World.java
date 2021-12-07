package simulation;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.pattern.StatusReply;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.brewery.Brewery;
import simulation.entities.employee.Employee;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.EmployeeMessage;
import systems.brewery.BreweryManagementSystem;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;
import systems.brewery.values.Recipe;
import systems.reference.ReferenceDataManagement;

import java.time.Duration;

public final class World extends AbstractBehavior<World.WorldMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(World.class);

    interface WorldMessage {
    }

    @AllArgsConstructor(staticName = "apply")
    public static class HelloWorld implements WorldMessage {

        ActorRef<StatusReply<Done>> done;

    }

    private final ActorContext<WorldMessage> ctx;

    private final ActorRef<EmployeeMessage> johnny;

    private World(ActorContext<WorldMessage> context, ActorRef<EmployeeMessage> johnny) {
        super(context);
        this.ctx = context;
        this.johnny = johnny;
    }

    public static Behavior<WorldMessage> create(ReferenceDataManagement refDataManagement, BreweryManagementSystem bms, Brewery brewery) {
        /*
         * Clear database.
         */
        bms.getBrews().clear();
        bms.getRecipes().clear();
        bms.getIngredientProducts().clear();
        bms.getIngredients().clear();
        LOG.info("Cleaned database.");

        /*
         * Configure initial data
         */
        systems.reference.model.Employee.predefined().forEach(refDataManagement::registerOrUpdateEmployee);
        Ingredient.predefined().forEach(ingredient -> bms.getIngredients().insertIngredient(ingredient));
        IngredientProduct.predefined().forEach(product -> bms.getIngredientProducts().insertIngredientProduct(product));
        Recipe.predefined().forEach(recipe -> bms.getRecipes().insertRecipe(recipe));
        LOG.info("Initial data inserted.");

        /*
         *
         */
        return Behaviors.setup(ctx -> {
            Clock
                .getInstance()
                .startSingleTimer("brew a beer", Duration.ofDays(1), done -> AskPattern
                    .ask(ctx.getSelf(), HelloWorld::apply, Duration.ofSeconds(10), ctx.getSystem().scheduler())
                    .thenApply(reply -> done.complete(reply.getValue())));

            var johnny = ctx.spawn(Employee.create(bms, systems.reference.model.Employee.johnny(), brewery), "johnny");
            return new World(ctx, johnny);
        });
    }

    @Override
    public Receive<WorldMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(HelloWorld.class, msg -> {
                AskPattern
                    .ask(
                        johnny,
                        (ActorRef<Done> ref) -> BrewABeerCommand.apply("foo", ref),
                        Duration.ofSeconds(10),
                        ctx.getSystem().scheduler())
                    .thenAccept(done -> msg.done.tell(StatusReply.success(done)));

                return Behaviors.same();
            })
            .build();
    }

}
