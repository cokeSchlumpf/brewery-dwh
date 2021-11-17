package systems.brewery;

import lombok.AllArgsConstructor;
import simulation.clock.Clock;
import systems.brewery.ports.BreweryRepositoryPort;

@AllArgsConstructor(staticName = "apply")
public class Brewery {

    private BreweryRepositoryPort repository;

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
        return 42.0;
    }

    public void startMashing() {
        System.out.println("start mashing" + Clock.getInstance().getNow());
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
