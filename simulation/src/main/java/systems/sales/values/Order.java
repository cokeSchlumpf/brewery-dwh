package systems.sales.values;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Order {

    /**
    * The customer that made the order
     **/
    Customer customer;

    /**
     * The time that the order was made
     */
    Instant order_time;

    /**
     * The time that the order was delivered/ executed
     */

    Instant delivery_time;

    /**
     * Items of the order
     */

    List<OrderItem> items;

    public static Order predefinedOrder(){
        Customer customer = Customer.apply("test@email.com");
        List<OrderItem> item = Lists.<OrderItem>newArrayList();
        item.add(OrderItem.predefinedOrderItem());
        return Order.apply(customer,Instant.now(), null, item);
    }
}
