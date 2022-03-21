package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.sales.values.Order;
import systems.sales.values.OrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class OrdersJdbcImpl implements Orders{

    private final Jdbi jdbi;

    private final Customers customers;

    private final StockProducts products;

    @Override
    public void clear() {
        var deleteOrderItems = Templates.renderTemplateFromResources("db/sql/sales/orders--order-items--delete.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(deleteOrderItems)
            .execute());

        var deleteOrders = Templates.renderTemplateFromResources("db/sql/sales/orders--delete.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(deleteOrders)
            .execute());
    }

    @Override
    public Optional<Order> findOrderById(int id) {
        var orderItems = getOrderItems(id);
        var query = Templates.renderTemplateFromResources("db/sql/sales/orders--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id)
            .map(OrderMapper.apply(customers, orderItems))
            .findFirst());
    }

    @Override
    public List<Order> getAllOrders() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/orders--select.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map((rs, ctx) -> rs.getInt("id"))
            .map(this::getOrderById)
            .list());
    }

    @Override
    public int insertOrder(int customerId, Instant orderTime, List<OrderItem> items) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/orders--insert.sql");

        var orderId = jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("customer", customerId)
            .bind("order_date", orderTime)
            .executeAndReturnGeneratedKeys("id")
            .map((rs, ctx) -> rs.getInt("id"))
            .first());

        items.forEach(item -> insertOrderItem(orderId, item));

        return orderId;
    }

    @Override
    public void updateOrder(Order order) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/orders--update.sql");

        var customerId = order.getCustomer().getId();

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", order.getOrderId())
            .bind("customer", customerId)
            .bind("order_date", order.getOrdered())
            .bind("delivery_date", order.getDelivered().orElse(null))
            .execute());
    }

    private void insertOrderItem(int orderId, OrderItem item) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/orders--order-items--insert.sql");

        var productId = products.getProductId(item.getBeer());

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", orderId)
            .bind("product", productId)
            .bind("quantity", item.getBottles())
            .execute());
    }

    private List<OrderItem> getOrderItems(int orderId) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/orders--order-items--select.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", orderId)
            .map(OrderItemMapper.apply(products))
            .stream()
            .collect(Collectors.toList()));
    }

    @AllArgsConstructor(staticName = "apply")
    private static class OrderMapper implements RowMapper<Order> {

        private final Customers customers;

        private final List<OrderItem> items;

        @Override
        public Order map(ResultSet rs, StatementContext ctx) throws SQLException {
            var delivery = rs.getTimestamp("delivery_date");
            if(delivery == null){
                return Order.apply(rs.getInt("id"), customers.getCustomerById(rs.getInt("customer")), rs.getTimestamp("order_date").toInstant(), null, items);
            }
            else{
                return Order.apply(rs.getInt("id"), customers.getCustomerById(rs.getInt("customer")), rs.getTimestamp("order_date").toInstant(), delivery.toInstant(), items);
            }

        }

    }

    @AllArgsConstructor(staticName = "apply")
    private static class OrderItemMapper implements RowMapper<OrderItem> {

        private final StockProducts products;

        @Override
        public OrderItem map(ResultSet rs, StatementContext ctx) throws SQLException {
            return OrderItem.apply(products.getProductById(rs.getInt("product")), rs.getInt("quantity"));
        }

    }

}
