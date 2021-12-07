package systems.brewery.values;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * A ingredient product is a concrete "implementation" or product which can be used as the ingredient.
 * E.g. Rice is an ingredient, Uncle Ben's Rice is the product.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class IngredientProduct {

    /**
     * The ingredient which is realized by this product.
     */
    Ingredient ingredient;

    /**
     * The id (order number) of the product, provided/ owned by the producer of the product.
     */
    String producerProductId;

    /**
     * The name of the producer.
     */
    String producerName;

    /**
     * The name of the product as named by the producer of the product.
     */
    String productName;

    public static List<IngredientProduct> predefined() {
        return Lists.newArrayList(
            hopFromDrOetker(),
            hopFromHinkelhubers(),
            hopFromSternquell(),
            hopFromHinkelhubersTrashLine(),
            maltFromDrOetker(),
            maltFromHinkelhubers(),
            maltFromWeihnstephan(),
            grapefruiteFromNestle(),
            grapefruitFromCocaCola(),
            glutamateFromChineseFastFoodInc(),
            beerWortFromSternquell(),
            beerWortWeihnstephan(),
            waterFromCityOfBamberg(),
            waterFromCocaCola(),
            lemonFromAldi()
        );
    }

    public static IngredientProduct hopFromHinkelhubers() {
        return IngredientProduct.apply(Ingredient.hop(), "hh-001", "Hinkelhuber", "Hinkel's Finest Hop");
    }

    public static IngredientProduct hopFromHinkelhubersTrashLine() {
        return IngredientProduct.apply(Ingredient.hop(), "hh-991", "Hinkelhuber", "Hinkel's Trash Hop for Chinese Beer");
    }

    public static IngredientProduct hopFromDrOetker() {
        return IngredientProduct.apply(Ingredient.hop(), "do-001", "Dr. Oetker", "Old Granny's Hop Mix");
    }

    public static IngredientProduct hopFromSternquell() {
        return IngredientProduct.apply(Ingredient.hop(), "sq-001", "Sternquell", "Vogtland Best Hop");
    }

    public static IngredientProduct maltFromHinkelhubers() {
        return IngredientProduct.apply(Ingredient.malt(), "hh-002", "Hinkelhuber", "Hinkel's Finest Malt");
    }

    public static IngredientProduct maltFromDrOetker() {
        return IngredientProduct.apply(Ingredient.malt(), "do-002", "Dr. Oetker", "Dr. Oetker's Bavarian Malt");
    }

    public static IngredientProduct maltFromWeihnstephan() {
        return IngredientProduct.apply(Ingredient.malt(), "ws-001", "Weihnstephan", "Our Malt is the real Bavarian! Don't trust the others!");
    }

    public static IngredientProduct grapefruitFromCocaCola() {
        return IngredientProduct.apply(Ingredient.grapefruit(), "cc-001", "Coca-Cola", "Grapefruit Extract");
    }

    public static IngredientProduct grapefruiteFromNestle() {
        return IngredientProduct.apply(Ingredient.grapefruit(), "ns-001", "Nestlé", "Grapefruite from best Spanish Quality (actually child labour in south america)");
    }

    public static IngredientProduct glutamateFromChineseFastFoodInc() {
        return IngredientProduct.apply(Ingredient.glutamate(), "cff-001", "Chinese Fast Food Inc.", "Real Taste");
    }

    public static IngredientProduct beerWortFromSternquell() {
        return IngredientProduct.apply(Ingredient.beerWort(), "sq--002", "Sternquell", "Vogtländische Stammwürze");
    }

    public static IngredientProduct beerWortWeihnstephan() {
        return IngredientProduct.apply(Ingredient.beerWort(), "ws-002", "Weihnstephan", "Bavarian Beer Wort");
    }

    public static IngredientProduct waterFromCityOfBamberg() {
        return IngredientProduct.apply(Ingredient.water(), "bb-001", "Bamberg", "Tasty water from our nature");
    }

    public static IngredientProduct waterFromCocaCola() {
        return IngredientProduct.apply(Ingredient.water(), "cc-002", "Coca-Cola", "Vio");
    }

    public static IngredientProduct lemonFromAldi() {
        return IngredientProduct.apply(Ingredient.lemon(), "aldi-001", "Aldi", "Lemon Extract");
    }

}
