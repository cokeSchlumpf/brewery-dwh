package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Beer;
import systems.sales.values.Product;

import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class OrdersJdbcImpl implements Orders{
    private final Jdbi jdbi;
}
