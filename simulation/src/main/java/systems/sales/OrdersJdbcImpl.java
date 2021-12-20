package systems.sales;

import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Order;
import systems.sales.values.OrderItem;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class OrdersJdbcImpl implements Orders{
    private final Jdbi jdbi;

    @Override
    public Optional<Order> findOrderById(int id) {
        return Optional.empty();
    }

    @Override
    public int insertOrder(int customerId, Instant orderTime, List<OrderItem> items) {
        return 0;
    }

    @Override
    public void updateOrder(Order order) {

    }

    @Override
    public List<Order> getAllOrders() {
        return null;
    }
}
