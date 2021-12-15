package systems.sales.values;

import common.P;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class OrderItem {

    /**
     * Name of the ordered beer
     */
    Product beer;

    /**
     * Order quantity of this item
     */
    int bottles;

    public static OrderItem predefinedOrderItem(){
        var item = P.randomItem(Product.predefinedBarBeer());
        var quantity = (int) P.randomDouble(item.getInventory()/2,1);
        return OrderItem.apply(item, quantity);
    }

    public String toString(){
        return beer.getProductName()+" "+bottles;
    }
}
