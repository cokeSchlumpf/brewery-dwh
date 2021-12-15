package simulation.entities.employee.state;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AskPattern;
import com.google.common.collect.Lists;
import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.employee.EmployeeContext;
import simulation.entities.employee.messages.*;

import java.time.Duration;


@AllArgsConstructor(staticName = "apply")
public final class IdleState implements State {

    private final EmployeeContext ctx;

    @Override
    public State onBrewABeerCommand(BrewABeerCommand cmd) {
        return BrewingState.apply(ctx, cmd);
    }

    @Override
    public State onCheckBeerSupplyCommand(CheckBeerSupply cmd){

        var inventory = ctx.getSalesManagementSystem().getBeers().getBeerProducts();
        inventory.removeIf(product -> product.getInventory() <= 0);

        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofMinutes(5)))
            .run(()->{
                ctx.log("Received Check Beer Supply Command");
            })
            .ask(cmd.getResponse(), ack -> CheckBeerSupplyResponse.apply(inventory,ack))
            .schedule();
        cmd.getAck().tell(Done.getInstance());

        return this;
    }

    @Override
    public State onBeerOrderCommand(BeerOrderCommand cmd) {

        ctx.log("Received Beer order");

        if(cmd.getOrder() == null){
            cmd.getAck().tell(Done.getInstance());
            return this;
        }
        ctx.log("Order: " + cmd.getOrder().getItems().toString());

        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofDays(2)))
            .run(()->{
                ctx.log("process beer order and ship to customer");
                cmd
                    .getOrder()
                    .getItems()
                    .forEach(product -> {
                        ctx.getSalesManagementSystem().getBeers().updateBeerProduct(product.getBeer().getProductName(), -1*product.getBottles());
                    });
            })
            .ask(cmd.getResponse(), ack -> SendBeerCommand.apply(ack))
            .schedule();

        cmd.getAck().tell(Done.getInstance());
        return this;
    }



}
