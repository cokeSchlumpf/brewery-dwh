package simulation.entities.onlinestore.messages;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.OnlineStore;
import systems.sales.values.Order;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An employee can check the orders of the online store to process them (package the beer and send it to the client).
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class CheckOpenOrders implements OnlineStore.Message {

    ActorRef<CheckOrdersResponse> orders;

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class CheckOrdersResponse {

        List<Order> orders;

        public List<Order> getOpenOrders() {
            return orders
                .stream()
                .filter(order -> order.getDelivered().isEmpty())
                .collect(Collectors.toList());
        }

    }

}
