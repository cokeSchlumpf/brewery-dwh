package simulation.entities.brewery.state;

import lombok.AllArgsConstructor;
import simulation.entities.brewery.BreweryContext;
import simulation.entities.brewery.values.HeatingLevel;

@AllArgsConstructor(staticName = "apply")
public final class Idle implements BreweryState {

    private final BreweryContext context;

    @Override
    public BreweryState prepareBrew() {
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
