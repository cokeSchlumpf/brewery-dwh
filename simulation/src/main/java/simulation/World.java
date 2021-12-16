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
import simulation.entities.customer.Customer;
import simulation.entities.customer.messages.AskBeerSupply;
import simulation.entities.customer.messages.CustomerMessage;
import simulation.entities.customer.values.CustomerType;
import simulation.entities.employee.Employee;
import simulation.entities.employee.messages.BrewABeerCommand;
import simulation.entities.employee.messages.CheckBeerSupply;
import simulation.entities.employee.messages.EmployeeMessage;
import simulation.entities.onlinestore.OnlineStore;
import systems.brewery.BreweryManagementSystem;
import systems.brewery.values.Ingredient;
import systems.brewery.values.IngredientProduct;
import systems.brewery.values.Recipe;
import systems.reference.ReferenceDataManagement;
import systems.sales.SalesManagementSystem;
import systems.sales.values.Beer;

import java.time.Duration;

public final class World extends AbstractBehavior<World.WorldMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(World.class);

    interface WorldMessage {
    }

    @AllArgsConstructor(staticName = "apply")
    public static class HelloWorld implements WorldMessage {

        ActorRef<StatusReply<Done>> done;

    }


    @AllArgsConstructor(staticName = "apply")
    public static class HelloCustomerWorld implements WorldMessage {

        ActorRef<StatusReply<Done>> done;

    }

    private final ActorContext<WorldMessage> ctx;

    private final ActorRef<EmployeeMessage> johnny;

    private final ActorRef<CustomerMessage> sam;

    private World(ActorContext<WorldMessage> context, ActorRef<EmployeeMessage> johnny, ActorRef<CustomerMessage> sam) {
        super(context);
        this.ctx = context;
        this.johnny = johnny;
        this.sam = sam;
    }

    public static Behavior<WorldMessage> create(ReferenceDataManagement refDataManagement, BreweryManagementSystem bms, Brewery brewery, SalesManagementSystem sms) {
        /*
         * Clear database.
         */
        bms.getBrews().clear();
        bms.getRecipes().clear();
        bms.getIngredientProducts().clear();
        bms.getIngredients().clear();
        sms.getBeers().clear();
        LOG.info("Cleaned database.");

        /*
         * Configure initial data
         */
        systems.reference.model.Employee.predefined().forEach(refDataManagement::registerOrUpdateEmployee);
        Ingredient.predefined().forEach(ingredient -> bms.getIngredients().insertIngredient(ingredient));
        IngredientProduct.predefined().forEach(product -> bms.getIngredientProducts().insertIngredientProduct(product));
        Recipe.predefined().forEach(recipe -> bms.getRecipes().insertRecipe(recipe));

        sms.getBeers().insertBeer(Beer.barBeerpredefined());
        sms.getBeers().insertBeer(Beer.fooBeerpredefined());

        LOG.info("Initial data inserted.");

        /*
         *
         */
        return Behaviors.setup(ctx -> {

            Clock
                .getInstance()
                .startSingleTimer("brew a beer", Duration.ofDays(1), done -> AskPattern
                    .ask(ctx.getSelf(), HelloCustomerWorld::apply, Duration.ofSeconds(10), ctx.getSystem().scheduler())
                    .thenApply(reply -> done.complete(reply.getValue()))
                );

            var johnny = ctx.spawn(Employee.create(bms,sms, systems.reference.model.Employee.johnny(), brewery), "johnny");

            var onlinestore = ctx.spawn(OnlineStore.create(sms,johnny), "brewery.com");
            var sam = ctx.spawn(Customer.create(johnny, onlinestore,CustomerType.NORMAL), "sam");
            return new World(ctx, johnny, sam);
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
            .onMessage(HelloCustomerWorld.class, msg -> {
                AskPattern
                        .ask(
                                sam,
                                (ActorRef<Done> ref) -> AskBeerSupply.apply(ref),
                                Duration.ofSeconds(10),
                                ctx.getSystem().scheduler())
                        .thenAccept(done -> msg.done.tell(StatusReply.success(done)));
                return Behaviors.same();
                })
            .build();
    }

}
