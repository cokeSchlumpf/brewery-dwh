package systems.sales.values;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class StockProduct {

    Product product;

    int amount;

    int reserved;

    public int getAmountAvailable() {
        return amount - reserved;
    }

}
