package simulation.entities.brewery.state;

import lombok.AllArgsConstructor;
import simulation.entities.brewery.BreweryContext;
import simulation.entities.brewery.values.HeatingLevel;
import systems.brewery.values.IngredientProduct;

@AllArgsConstructor(staticName = "apply")
public final class Sparging implements BreweryState {

    private final BreweryContext context;

    @Override
    public BreweryState addIngredient(IngredientProduct ingredient, double amount) {
        // this is ok, but we do nothing,
        return this;
    }

    @Override
    public BreweryState setHeating(HeatingLevel level) {
        this.context.setHeating(level);
        return this;
    }

    @Override
    public BreweryState startSparging() {
        return this;
    }

    @Override
    public BreweryState stopSparging() {
        return Brewing.apply(context);
    }

    @Override
    public double readTemperature() {
        return context.readTemperature();
    }

    @Override
    public HeatingLevel readHeatingLevel() {
        return context.getHeatingLevel();
    }

}
