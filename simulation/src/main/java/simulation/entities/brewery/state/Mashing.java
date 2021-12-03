package simulation.entities.brewery.state;

import lombok.AllArgsConstructor;
import simulation.entities.brewery.BreweryContext;
import simulation.entities.brewery.values.HeatingLevel;
import systems.brewery.values.IngredientProduct;

@AllArgsConstructor(staticName = "apply")
public final class Mashing implements BreweryState {

    private final BreweryContext context;


    @Override
    public BreweryState addIngredient(IngredientProduct ingredient, double amount) {
        // It's ok, but we do nothing.
        return this;
    }

    @Override
    public BreweryState setHeating(HeatingLevel level) {
        context.setHeating(level);
        return this;
    }

    @Override
    public BreweryState startMashing() {
        return this;
    }

    @Override
    public BreweryState stopMashing() {
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
