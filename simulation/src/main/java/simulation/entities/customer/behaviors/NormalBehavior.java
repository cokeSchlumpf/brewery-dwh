package simulation.entities.customer.behaviors;

import common.P;
import systems.sales.values.StockProduct;
import systems.sales.values.OrderItem;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public final class NormalBehavior extends CustomerBehavior {

    private final List<String> favoriteBeers;

    private NormalBehavior(CustomerProperties properties, List<String> favoriteBeers) {
        super(properties);
        this.favoriteBeers = favoriteBeers;
    }

    public static NormalBehavior apply(CustomerProperties properties, List<String> favoriteBeers) {
        return new NormalBehavior(properties, favoriteBeers);
    }

    @Override
    public List<OrderItem> generateOrder(List<StockProduct> productsOffered) {
        return productsOffered
            .stream()
            .filter(product -> favoriteBeers.contains(product.getProduct().getProductName()))
            .limit(3)
            .map(product -> OrderItem.apply(
                product.getProduct(),
                Math.min(P.randomInteger(12, 3.0), product.getAmountAvailable())))
            .collect(Collectors.toList());
    }

    @Override
    public Duration getNextOrderDelay() {
        return P.randomDuration(Duration.ofDays(7), Duration.ofDays(1));
    }
}
