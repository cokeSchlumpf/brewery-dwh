package simulation.entities.brewery;

import lombok.AllArgsConstructor;
import simulation.entities.brewery.state.BreweryState;
import simulation.entities.brewery.state.Idle;
import simulation.entities.brewery.state.Initializing;
import simulation.entities.brewery.values.HeatingLevel;
import systems.brewery.values.IngredientProduct;

@AllArgsConstructor(staticName = "apply")
public final class Brewery {

    private BreweryState state;

    public static Brewery apply() {
        var brewery = Brewery.apply(Initializing.apply());
        var ctx = BreweryContext.apply(brewery, 21.3);

        brewery.state = Idle.apply(ctx);
        return brewery;
    }

    public void prepareBrew() {
        this.state = state.prepareBrew();
    }

    public void addIngredient(IngredientProduct ingredient, double amount) {
        this.state = state.addIngredient(ingredient, amount);
    }

    public void setHeating(HeatingLevel level) {
        this.state = state.setHeating(level);
    }

    public void startMashing() {
        this.state = state.startMashing();
    }

    public void stopMashing() {
        this.state = state.stopMashing();
    }

    public void startSparging() {
        this.state = state.startSparging();
    }

    public void stopSparging() {
        this.state = state.stopSparging();
    }

    public void finish() {
        this.state = state.finish();
    }

    public HeatingLevel getHeatingLevel() { return state.readHeatingLevel(); }

    public double readTemperature() {
        return state.readTemperature();
    }

}
