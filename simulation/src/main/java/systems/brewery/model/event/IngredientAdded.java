package systems.brewery.model.event;

import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.model.IngredientProduct;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class IngredientAdded {

    Instant moment;

    IngredientProduct product;

    double amount;

}
