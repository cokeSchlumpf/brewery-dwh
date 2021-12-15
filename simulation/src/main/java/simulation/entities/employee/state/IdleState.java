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
import systems.brewery.Brews;
import systems.brewery.values.Bottling;
import systems.brewery.values.Brew;
import systems.sales.values.Beer;
import systems.sales.values.Product;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class IdleState implements State {

    private final EmployeeContext ctx;

    @Override
    public State onBrewABeerCommand(BrewABeerCommand cmd) {
        return BrewingState.apply(ctx, cmd);
    }

    @Override
    public State onCheckBeerSupplyCommand(CheckBeerSupply cmd){


        List<List<Product>> inventory = Lists.<List<Product>>newArrayList();
        inventory.add(Beer.fooBeerpredefined().getProducts());
        inventory.add(Beer.barBeerpredefined().getProducts());
        List<Product> flattened_inventory = inventory.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofMinutes(5)))
            .run(()->{
                ctx.log("Received Check Beer Supply Command");
            })
            .ask(cmd.getResponse(), ack -> CheckBeerSupplyResponse.apply(flattened_inventory,ack))
            .schedule();
        cmd.getAck().tell(Done.getInstance());

        return this;
    }

    @Override
    public State onBeerOrderCommand(BeerOrderCommand cmd) {

        ctx.log("Received Beer order");
        ctx.log("Order: " + cmd.getOrder().getItems().toString());
        // Process order

        Clock
                .scheduler(ctx.getActor())
                .waitFor(P.randomDuration(Duration.ofDays(2)))
                .run(()->{
                    ctx.log("process beer order and ship to customer");
                })
                .ask(cmd.getResponse(), ack -> SendBeerCommand.apply(ack))
                .schedule();

        cmd.getAck().tell(Done.getInstance());

        return this;
    }



}
