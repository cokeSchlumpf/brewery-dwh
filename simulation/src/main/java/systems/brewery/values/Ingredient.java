package systems.brewery.values;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Ingredient {

    String name;

    String unit;

    public static List<Ingredient> predefined() {
        return Lists.newArrayList(
            hop(),
            malt(),
            grapefruit(),
            glutamate(),
            beerWort(),
            water(),
            lemon());
    }

    public static Ingredient hop() {
        return Ingredient.apply("hop", "kg");
    }

    public static Ingredient malt() {
        return Ingredient.apply("malt", "kg");
    }

    public static Ingredient grapefruit() {
        return Ingredient.apply("grapefruit", "l");
    }

    public static Ingredient glutamate() {
        return Ingredient.apply("glutamate", "g");
    }

    public static Ingredient beerWort() {
        return Ingredient.apply("beer wort", "g");
    }

    public static Ingredient water() {
        return Ingredient.apply("water", "l");
    }

    public static Ingredient lemon() {
        return apply("lemon", "l");
    }

}
