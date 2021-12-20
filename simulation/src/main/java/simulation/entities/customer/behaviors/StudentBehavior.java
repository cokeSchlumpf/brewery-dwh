package simulation.entities.customer.behaviors;

import common.P;
import systems.sales.values.StockProduct;
import systems.sales.values.OrderItem;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class StudentBehavior extends CustomerBehavior {

    private final List<String> favoriteBeers;

    private StudentBehavior(CustomerProperties properties, List<String> favoriteBeers) {
        super(properties);
        this.favoriteBeers = favoriteBeers;
    }

    public static StudentBehavior apply(CustomerProperties properties, List<String> favoriteBeers) {
        return new StudentBehavior(properties, favoriteBeers);
    }

    @Override
    public List<OrderItem> generateOrder(List<StockProduct> productsOffered) {
        return productsOffered
            .stream()
            .sorted(Comparator.comparing(p -> p.getProduct().getPrice()))
            .filter(product -> (favoriteBeers.contains(product.getProduct().getProductName()) && product.getProduct().getPrice() < 1.0) || product.getProduct().getPrice() < 0.7)
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
