package simulation.entities.customer.behaviors;

import common.P;
import simulation.entities.onlinestore.messages.PlaceOrder;
import systems.sales.values.Address;
import systems.sales.values.StockProduct;
import systems.sales.values.OrderItem;

import java.time.Duration;
import java.util.List;

public abstract class CustomerBehavior {

    private CustomerProperties properties;

    private Integer customerId;

    public CustomerBehavior(CustomerProperties properties) {
        this.properties = properties;
        this.customerId = null;
    }

    public abstract List<OrderItem> generateOrder(List<StockProduct> products);

    public PlaceOrder.Customer getCustomer() {
        if (customerId == null && P.randomBoolean(0.2)) {
            var address = Address.apply(
                properties.getStreet().getValue(),
                properties.getZipCode().getValue(),
                properties.getCity().getValue());

            return PlaceOrder.NewCustomer.apply(
                properties.getEmail().getValue(),
                properties.getFirstName().getValue(),
                properties.getLastName().getValue(),
                address);
        } else {
            return PlaceOrder.RegisteredCustomer.apply(customerId);
        }
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Duration getNextOrderDelay() {
        return Duration.ofDays(7);
    }

}
