package simulation.entities.brewery.values;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Value
@AllArgsConstructor(staticName = "apply")
public class HeatingPhysics {

    Instant since;

    double initialTemperature;

    double outsideTemperature;

    HeatingLevel level;

    public double getTemperatureAt(Instant moment) {
        var heatingSlope = getHeatingSlope();
        var coolingSlope = getCoolingSlope();
        var amount = ChronoUnit.MINUTES.between(since, moment);

        return Math.min(initialTemperature + amount * heatingSlope - amount * coolingSlope, 100);
    }

    private double getCoolingSlope() {
        var diff = outsideTemperature - initialTemperature;
        return diff / 42;
    }

    private double getHeatingSlope() {
        switch(this.level){
            case L01_LOWEST_HEAT:
                return 0.27;
            case L02_VERY_LOW_HEAT:
                return 0.6;
            case L03_LOW_HEAT:
                return 1.13;
            case L04_MEDIUM_LOW_HEAT:
                return 3.17;
            case L07_MEDIUM_HEAT:
                return 4.82;
            case L08_MEDIUM_HIGH_HEAT:
                return 9.47;
            case L09_HIGH_HEAT:
                return 13.19;
            default:
                return 0;
        }
    }

}
