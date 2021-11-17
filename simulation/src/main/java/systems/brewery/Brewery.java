package systems.brewery;

import common.P;
import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import systems.brewery.ports.BreweryRepositoryPort;
import systems.brewery.values.brewery.MashHeatingLevel;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor(staticName = "apply")
public class Brewery {

    private BreweryRepositoryPort repository;
    private MashHeatingLevel heatinglevel;
    private LocalDateTime start_mashing;
    private double start_temperature;

    public void startBrewing(String beerKey, String employeeId, int originalGravity) {
        System.out.println("Start brewing ..." + Clock.getInstance().getNow());
    }

    public void addIngredient(String ingredientProductName, String ingredientProducer, double amount) {
        System.out.println("Add ingredient ..." + Clock.getInstance().getNow());
    }

    public void startBoiling() {
        System.out.println("Start boiling ..." + Clock.getInstance().getNow());
    }

    public void stopBoiling() {
        System.out.println("Stop boiling ... " + Clock.getInstance().getNow());
    }

    public double readMashingTemperature() {
        System.out.println("Read mashing temperature");

        double slope;
        switch(this.heatinglevel){
            case LOW_HEAT: slope = 0.15;break;
            case LOW_MEDIUM_HEAT: slope = 0.35;break;
            case MEDIUM_HEAT: slope = 0.55;break;
            case MEDIUM_HIGH_HEAT: slope = 0.75;break;
            default: slope = 0.85;break;
        }
        int time_passed = Clock.getInstance().getNow().getMinute() - this.start_mashing.getMinute();
        double temp = P.randomDouble(this.start_temperature + slope * time_passed, 5);
        return temp;
    }

    public void setHeatLevel(MashHeatingLevel level){
        System.out.println("Set heat level for mashing to " + level + " "+ Clock.getInstance().getNow());
        this.heatinglevel = level;
    }

    public void startMashing(MashHeatingLevel level, double start_temperature) {
        System.out.println("start mashing" + Clock.getInstance().getNow());
        setHeatLevel(level);
        this.start_mashing = Clock.getInstance().getNow();
        this.start_temperature = start_temperature;
    }

    public void stopMashing() {
        System.out.println("stop mashing" + Clock.getInstance().getNow());
    }

    public void startSparging() {
        System.out.println("start sparging");
    }

    public void stopSparging() {
        System.out.println("stop sparging");
    }

}
