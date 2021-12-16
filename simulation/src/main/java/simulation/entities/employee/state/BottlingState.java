package simulation.entities.employee.state;

import akka.Done;
import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.employee.EmployeeContext;
import simulation.entities.employee.messages.BottlingBrewCommand;
import systems.sales.values.Bottling;

import java.time.Duration;


@AllArgsConstructor(staticName = "apply")
public class BottlingState implements State{

    private final EmployeeContext ctx;

    @Override
    public State onBottlingBrewCommand(BottlingBrewCommand cmd){
        // ToDo: Rekursiv, sodass bottlings zu unterschiedlichen Zeiten gebottled werden.
        ctx.log("Bottling");

        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofMinutes(30)))
            .run((now) -> {
                // Get the products that are offered for this beer id
                var products_of_beer =  ctx.getSalesManagementSystem().getBeers().readBeerProductsByBeerId(cmd.getBeer_id());
                // ToDo: If empty: insert as new product
                // For every product of this beer, a bottling is created.
                var left_beer = cmd.getVolume().doubleValue();
                for(int i = 0; i < products_of_beer.size(); i ++){
                    var bottled = now;
                    var bbf = cmd.getBrewing_time().plus(P.randomDuration(Duration.ofDays(7 * 31), Duration.ofDays(2 * 31)));
                    Bottling bottling;
                    if(i == products_of_beer.size()-1){
                        bottling = Bottling.apply(bottled, bbf, (int) left_beer, (int) (left_beer/products_of_beer.get(i).getVolume()));
                    }
                    else{
                        var quantity = P.randomDouble(cmd.getVolume()/products_of_beer.size(),20);
                        bottling = Bottling.apply(bottled, bbf, (int) quantity, (int) (quantity/products_of_beer.get(i).getVolume()));
                        left_beer = left_beer - quantity;
                        if(left_beer <= 0) break;
                    }
                    ctx
                        .getSalesManagementSystem()
                        .getBeers()
                        .logBottling(products_of_beer.get(i).getProductName(),bottling);
                }
            })
            .schedule();
        cmd.getAck().tell(Done.getInstance());
        return IdleState.apply(ctx);
    }
}
