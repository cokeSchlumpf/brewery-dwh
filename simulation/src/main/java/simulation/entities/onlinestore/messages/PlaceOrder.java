package simulation.entities.onlinestore.messages;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.onlinestore.OnlineStore;
import systems.sales.values.Address;
import systems.sales.values.OrderItem;

import java.util.List;

/**
 * A customer can place an order to the online shop with this message.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class PlaceOrder implements OnlineStore.Message {

    /**
     * The id of the customer, this value might be null.
     */
    Customer customer;

    /**
     * Items of the order
     */
    List<OrderItem> items;

    /**
     * Acknowledge that the order has been received.
     */
    ActorRef<PlaceOrderResponse> confirmTo;

    /**
     * Sealed interface to provide different options to pass customer information.
     */
    public interface Customer { }

    /**
     * Pass an already known customer id.
     */
    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class RegisteredCustomer implements Customer {

        int id;

    }

    /**
     * Pass customer information without customer id (new customer or forgotten).
     */
    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class NewCustomer implements Customer {

        /**
         * The e-mail address of the customer.
         **/
        String email;

        /**
         * The first name of the customer.
         */
        String firstname;

        /**
         * The last name of the customer.
         */
        String name;

        /**
         * The address of the customer, it is used for invoice and shipping.
         */
        Address address;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class PlaceOrderResponse {

        /**
         * The newly created or mapped customer id which is used for the order.
         */
        int customerId;

        /**
         * The order id which has been created for the new order.
         */
        int orderId;

    }

}
