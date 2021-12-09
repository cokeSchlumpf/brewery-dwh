package simulation.entities.employee.state;

import akka.Done;
import akka.actor.typed.ActorRef;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.employee.EmployeeContext;
import simulation.entities.employee.messages.*;
import systems.brewery.Brews;
import systems.brewery.values.Bottling;
import systems.brewery.values.Brew;
import systems.sales.values.Beer;
import systems.sales.values.Product;

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

        ctx.log("Received Check Beer Supply Command");

        // get inventory
        List<List<Product>> inventory = Lists.<List<Product>>newArrayList();
        inventory.add(Beer.fooBeerpredefined().getProducts());
        inventory.add(Beer.barBeerpredefined().getProducts());
        List<Product> flattened_inventory = inventory.stream()
                                                    .flatMap(List::stream)
                                                    .collect(Collectors.toList());
        cmd.getResponse().tell(CheckBeerSupplyResponse.apply(flattened_inventory,cmd.getAck()));

        return this;
    }

    @Override
    public State onBeerOrderCommand(BeerOrderCommand cmd) {

        ctx.log("Received Beer order");
        // Process order
        cmd.getResponse().tell(SendBeerCommand.apply(cmd.getAck()));
        return this;
    }



}
