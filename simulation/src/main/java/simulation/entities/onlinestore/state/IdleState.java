package simulation.entities.onlinestore.state;

import akka.Done;
import akka.actor.typed.ActorRef;
import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import simulation.entities.customer.messages.CustomerMessage;
import simulation.entities.customer.messages.ReceiveBeerFromOnlineStore;
import simulation.entities.employee.messages.BeerOrderCommand;
import simulation.entities.employee.messages.PrepareOrderToShipCommand;
import simulation.entities.employee.messages.ShipOnlineStoreBeerCommand;
import simulation.entities.onlinestore.OnlineStoreContext;
import simulation.entities.onlinestore.messages.GetBeerStoreRequest;
import simulation.entities.onlinestore.messages.GetBeerStoreResponse;
import simulation.entities.onlinestore.messages.PutOrderRequest;

import java.time.Duration;

@AllArgsConstructor(staticName = "apply")
public class IdleState implements State{

    private final OnlineStoreContext ctx;

    @Override
    public State onGetBeerStoreRequest(GetBeerStoreRequest cmd) {

        ctx.log("Received a get request");

        var inventory = ctx.getSalesManagementSystem().getBeers().readBeerProducts();

        inventory.removeIf(product -> {return (product.getInventory()<=0);});

        ctx.log(inventory.toString());
        Clock
            .scheduler(ctx.getActor())
            .waitFor(P.randomDuration(Duration.ofMinutes(2)))
            .ask(cmd.getResponse(), ack -> GetBeerStoreResponse.apply(inventory, ack))
            .scheduleAndAcknowledge(cmd.getAck());
        return this;
    }

    @Override
    public State onPutOrderRequest(PutOrderRequest cmd) {
        ctx.log("Online store received the Order");

        //remove beer from storage system, so that nobody else can order it
        // vtl. auch erst "reservieren"?

        // tell employee to process/ship order -> (employee needs the ref of customer?)
        // ctx.getEmployee().tell(PrepareOrderToShipCommand.apply(cmd.getAck(),cmd.getOrder(), cmd.getRecipient().messageAdapter(ShipOnlineStoreBeerCommand.class, ReceiveBeerFromOnlineStore::apply)));
        ctx.log(cmd.getOrder().getItems().toString());
        ctx.getEmployee().tell(PrepareOrderToShipCommand.apply(cmd.getAck(),cmd.getOrder(),cmd.getRecipient()));

        //cmd.getAck().tell(Done.getInstance());
        //ctx.getEmployee().tell(BeerOrderCommand.apply(cmd.getAck()))

        return this;
    }

}
