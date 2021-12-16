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
     * All bottlings for this beer product
     */

    List<Bottling> bottlings;

    public static List<Product> predefinedFooBeer(){
        var fooBeers = Lists.<Product>newArrayList();
        fooBeers.add(Product.apply("Small Foo Beer",0.7,0.33,Bottling.predefinedBottlings()));
        fooBeers.add(Product.apply("Extra Large Foo Beer",1.5,0.75,Bottling.predefinedBottlings()));
        fooBeers.add(Product.apply("Christmas Foo Beer",1.0,0.33,Bottling.predefinedBottlings()));
        return fooBeers;
    }

    public static List<Product> predefinedBarBeer(){
        var barBeers = Lists.<Product>newArrayList();
        barBeers.add(Product.apply("Standard Bar Beer",2.0,0.5,Bottling.predefinedBottlings()));
        barBeers.add(Product.apply("New Year's Bar Beer",1.5,0.33,Bottling.predefinedBottlings()));
        return barBeers;
    }

    public int getInventory(){
        var inventory = bottlings.stream().mapToInt(o -> o.getBottles()).sum();
        return inventory;
    }

}
