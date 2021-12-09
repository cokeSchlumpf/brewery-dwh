package systems.sales.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Beer {
    /**
     * The beer key of the product
     */
    String beerKey;

    /**
     * The name of the product.
     */
    String beerName;

    /**
     * List of actual products of this type.
     */
    List<Product> products;

    public static Beer fooBeerpredefined(){
        var products = Product.predefinedFooBeer();
        return Beer.apply("foo", "Foo Beer", products);
    }

    public static Beer barBeerpredefined(){
        var products = Product.predefinedBarBeer();
        return Beer.apply("bar", "Bar Beer", products);
    }
}
