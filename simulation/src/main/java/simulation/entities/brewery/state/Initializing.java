package simulation.entities.brewery.state;

import lombok.AllArgsConstructor;
import simulation.entities.brewery.values.HeatingLevel;

@AllArgsConstructor(staticName = "apply")
public final class Initializing implements BreweryState {

    @Override
    public double readTemperature() {
        return 0;
    }

    @Override
    public HeatingLevel readHeatingLevel() {
        return HeatingLevel.L00_OFF;
    }

}
