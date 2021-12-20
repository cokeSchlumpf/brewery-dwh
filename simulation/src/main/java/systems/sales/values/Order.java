package systems.sales.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import systems.sales.Orders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Order {

    /**
     * A unique id for the order.
     */
    int orderId;

    /**
     * The customer that made the order
     **/
    Customer customer;

    /**
     * The time that the order was made
     */
    Instant ordered;


    /**
     * The time that the order was delivered/ executed. This value might be null if order has not been delivered yet.
     */
    Instant delivered;

    /**
     * Items of the order
     */
    List<OrderItem> items;

    public static Order apply(int orderId, Customer customer, Instant ordered, List<OrderItem> items) {
        return apply(orderId, customer, ordered, null, items);
    }

    public Optional<Instant> getDelivered() {
        return Optional.ofNullable(delivered);
    }

}
