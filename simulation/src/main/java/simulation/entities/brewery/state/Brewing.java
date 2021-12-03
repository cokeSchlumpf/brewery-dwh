package simulation.entities.brewery.state;

import lombok.AllArgsConstructor;
import simulation.entities.brewery.BreweryContext;
import simulation.entities.brewery.values.HeatingLevel;
import systems.brewery.values.IngredientProduct;

@AllArgsConstructor(staticName = "apply")
public final class Brewing implements BreweryState {

    private final BreweryContext context;

    @Override
    public BreweryState addIngredient(IngredientProduct ingredient, double amount) {
        // do nothing but it's ok
        return this;
    }

    @Override
    public BreweryState setHeating(HeatingLevel level) {
        this.context.setHeating(level);
        return this;
    }

    @Override
    public BreweryState startMashing() {
        return Mashing.apply(context);
    }

    @Override
    public BreweryState startSparging() {
        return Sparging.apply(context);
    }

    @Override
    public BreweryState finish() {
        context.withHeating(null);
        return Idle.apply(context);
    }

    @Override
    public double readTemperature() {
        return this.context.readTemperature();
    }

    @Override
    public HeatingLevel readHeatingLevel() {
        return this.context.getHeatingLevel();
    }

}
