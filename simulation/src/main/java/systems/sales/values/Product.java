package systems.sales.values;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

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
     * The price per unit of the product.
     */

    Double price;

    /**
     * The volume (in liters) of one unit.
     */

    Double volume;

    /**
     * Number of units that are in stock.
     */

    Integer inventory;

    //ToDo: Inventory als Liste von Bottlings wegen mhb

    public static List<Product> predefinedFooBeer(){
        var fooBeers = Lists.<Product>newArrayList();
        fooBeers.add(Product.apply("Small Foo Beer",0.7,0.33,0));
        fooBeers.add(Product.apply("Extra Large Foo Beer",1.5,0.75,20));
        fooBeers.add(Product.apply("Christmas Foo Beer",1.0,0.33,20));
        return fooBeers;
    }

    public static List<Product> predefinedBarBeer(){
        var barBeers = Lists.<Product>newArrayList();
        barBeers.add(Product.apply("Standard Bar Beer",2.0,0.5,0));
        barBeers.add(Product.apply("New Year's Bar Beer",1.5,0.33,20));
        return barBeers;
    }

}
