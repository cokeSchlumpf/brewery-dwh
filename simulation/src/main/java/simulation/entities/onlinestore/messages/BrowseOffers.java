package simulation.entities.onlinestore.messages;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.OnlineStore;
import systems.sales.values.StockProduct;

import java.util.List;

/**
 * A potential customer may ask/ heck the online store for available offers.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class BrowseOffers implements OnlineStore.Message {

    ActorRef<BrowseOffersResponse> response;

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class BrowseOffersResponse {

        List<StockProduct> inventory;

    }

}
