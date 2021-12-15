package systems.sales;
import common.configs.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

@Getter
@AllArgsConstructor(staticName = "apply")
public class SalesManagementSystem {

    private final Beers beers;

    private final Orders orders;

    public static SalesManagementSystem apply(DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        var beers = BeersJdbcImpl.apply(jdbi);
        var orders = OrdersJdbcImpl.apply(jdbi);
        return apply(beers, orders);
    }
}
