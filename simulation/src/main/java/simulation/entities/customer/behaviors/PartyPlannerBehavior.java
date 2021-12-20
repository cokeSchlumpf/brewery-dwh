package simulation.entities.customer.behaviors;

import common.P;
import systems.sales.values.StockProduct;
import systems.sales.values.OrderItem;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public final class PartyPlannerBehavior extends CustomerBehavior {

    private PartyPlannerBehavior(CustomerProperties properties) {
        super(properties);
    }

    public static PartyPlannerBehavior apply(CustomerProperties properties) {
        return new PartyPlannerBehavior(properties);
    }

    @Override
    public List<OrderItem> generateOrder(List<StockProduct> productsOffered) {
        return P
            .nRandomItems(productsOffered, P.randomInteger(2, 0.5))
            .stream()
            .map(product -> OrderItem.apply(product.getProduct(), Math.min(P.randomInteger(50, 10.0), product.getAmountAvailable())))
            .collect(Collectors.toList());
    }

    @Override
    public Duration getNextOrderDelay() {
        return P.randomDuration(Duration.ofDays(120), Duration.ofDays(30));
    }

}
