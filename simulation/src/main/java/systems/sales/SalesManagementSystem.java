package systems.sales;
import common.configs.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

@Getter
@AllArgsConstructor(staticName = "apply")
public class SalesManagementSystem {

    private final StockProducts products;

    private final Orders orders;

    private final Customers customers;

    public static SalesManagementSystem apply(DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        var products = StockProductsJdbcImpl.apply(jdbi);
        var customers = CustomersJdbcImpl.apply(jdbi);
        var orders = OrdersJdbcImpl.apply(jdbi, customers, products);

        return apply(products, orders, customers);
    }
}
