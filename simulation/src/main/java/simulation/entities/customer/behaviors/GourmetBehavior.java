package simulation.entities.customer.behaviors;

import common.P;
import systems.sales.values.StockProduct;
import systems.sales.values.OrderItem;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public final class GourmetBehavior extends CustomerBehavior {

    private GourmetBehavior(CustomerProperties properties) {
        super(properties);
    }

    public static GourmetBehavior apply(CustomerProperties properties) {
        return new GourmetBehavior(properties);
    }

    @Override
    public List<OrderItem> generateOrder(List<StockProduct> productsOffered) {
        return P
            .nRandomItems(productsOffered, P.randomInteger(5, 3.0))
            .stream()
            .map(product -> OrderItem.apply(product.getProduct(), Math.min(P.randomInteger(6, 1.0), product.getAmountAvailable())))
            .collect(Collectors.toList());
    }

    @Override
    public Duration getNextOrderDelay() {
        return P.randomDuration(Duration.ofDays(30), Duration.ofDays(12));
    }
}
