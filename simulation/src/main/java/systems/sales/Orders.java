package systems.sales;

import systems.sales.values.Order;
import systems.sales.values.OrderItem;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface Orders {

    void clear();

    Optional<Order> findOrderById(int id);

    default Order getOrderById(int id) {
        return findOrderById(id).orElseThrow();
    }

    int insertOrder(int customerId, Instant orderTime, List<OrderItem> items);

    void updateOrder(Order order);

    List<Order> getAllOrders();

}
