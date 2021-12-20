package systems.sales;
import common.configs.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

@Getter
@AllArgsConstructor(staticName = "apply")
public class SalesManagementSystem {

    private final StockProducts stockProducts;

    private final Orders orders;

    private final Customers customers;

    public static SalesManagementSystem apply(DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        var beers = StockProductsJdbcImpl.apply(jdbi);
        var orders = OrdersJdbcImpl.apply(jdbi);
        var customers = (Customers) null;

        return apply(beers, orders, customers);
    }
}
