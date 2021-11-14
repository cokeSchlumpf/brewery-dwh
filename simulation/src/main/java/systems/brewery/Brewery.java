package systems.brewery;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "apply")
public class Brewery {

    public void startBrewing(String beerKey, String employeeId, int originalGravity) {
        throw new RuntimeException("not implemented");
    }

    public void addIngredient(String ingredientProductName, String ingredientProducer, double amount) {
        throw new RuntimeException("not implemented");
    }

    public void startBoiling() {
        throw new RuntimeException("not implemented");
    }

    public void stopBoiling() {
        throw new RuntimeException("not implemented");
    }

    public double readMashingTemperature() {
        throw new RuntimeException("not implemented");
    }

    public void startMashing() {
        throw new RuntimeException("not implemented");
    }

    public void stopMashing() {
        throw new RuntimeException("not implemented");
    }

}
