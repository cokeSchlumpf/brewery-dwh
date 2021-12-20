package systems.sales.values;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import systems.brewery.values.Recipe;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Product {

    /**
     * Name of the product
     */
    String productName;

    /**
     * Name of the beer (recipe) which is filled in this product.
     */
    String beerId;

    /**
     * The price per unit of the product.
     */
    Double price;

    /**
     * The volume (in liters) of one unit.
     */
    Double volume;


    public static List<Product> predefined() {
        var result = Lists.<Product>newArrayList();
        result.addAll(predefinedBarBeer());
        result.addAll(predefinedBarBeer());
        return result;
    }

    public static List<Product> predefinedFooBeer() {
        var fooBeers = Lists.<Product>newArrayList();
        fooBeers.add(Product.apply("Small Foo Beer", Recipe.fooBeer().getBeerKey(), 0.7, 0.33));
        fooBeers.add(Product.apply("Extra Large Foo Beer", Recipe.fooBeer().getBeerKey(), 1.5, 0.75));
        fooBeers.add(Product.apply("Christmas Foo Beer", Recipe.fooBeer().getBeerKey(), 1.0, 0.33));
        return fooBeers;
    }

    public static List<Product> predefinedBarBeer() {
        var barBeers = Lists.<Product>newArrayList();
        barBeers.add(Product.apply("Standard Bar Beer", Recipe.barBeer().getBeerKey(), 2.0, 0.5));
        barBeers.add(Product.apply("New Year's Bar Beer", Recipe.barBeer().getBeerKey(), 1.5, 0.33));
        return barBeers;
    }

}
