package systems.sales;

import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;

@AllArgsConstructor(staticName = "apply")
public class OrdersJdbcImpl implements Orders{
    private final Jdbi jdbi;
}
