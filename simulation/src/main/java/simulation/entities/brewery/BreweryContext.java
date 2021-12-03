package simulation.entities.brewery;

import lombok.AllArgsConstructor;
import lombok.With;
import simulation.clock.Clock;
import simulation.entities.brewery.values.HeatingLevel;
import simulation.entities.brewery.values.HeatingPhysics;

@With
@AllArgsConstructor(staticName = "apply")
public final class BreweryContext {

    private final Brewery self;

    private final double roomTemperature;

    private HeatingPhysics heating;

    public static BreweryContext apply(Brewery self, double roomTemperature) {
        return apply(self, roomTemperature, null);
    }

    public HeatingLevel getHeatingLevel() {
        if (heating == null) {
            return HeatingLevel.L00_OFF;
        } else {
            return heating.getLevel();
        }
    }

    public void setHeating(HeatingLevel level) {
        if (this.heating == null) {
            this.heating = HeatingPhysics.apply(
                Clock.getInstance().getNowAsInstant(),
                roomTemperature,
                roomTemperature,
                level);
        } else {
            this.heating = HeatingPhysics.apply(
                Clock.getInstance().getNowAsInstant(),
                this.heating.getTemperatureAt(Clock.getInstance().getNowAsInstant()),
                roomTemperature,
                level);
        }
    }

    public double readTemperature() {
        if (this.heating != null) {
            return this.heating.getTemperatureAt(Clock.getInstance().getNowAsInstant());
        } else {
            return roomTemperature;
        }
    }

}
