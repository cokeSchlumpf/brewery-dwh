package simulation.entities.brewery.state;

import simulation.entities.brewery.values.HeatingLevel;
import systems.brewery.values.IngredientProduct;

public interface BreweryState {

    default BreweryState prepareBrew() {
        throw new RuntimeException(String.format("I cannot prepare a brew in this state `%s`.", this.getClass()));
    }

    default BreweryState addIngredient(IngredientProduct ingredient, double amount) {
        throw new RuntimeException(String.format("I cannot add an ingredient in this state `%s`.", this.getClass()));
    }

    default BreweryState setHeating(HeatingLevel level) {
        throw new RuntimeException(String.format("I cannot set a heating level in this state `%s`.", this.getClass()));
    }

    default BreweryState startMashing() {
        throw new RuntimeException(String.format("I cannot start mashing in this state `%s`.", this.getClass()));
    }

    default BreweryState stopMashing() {
        throw new RuntimeException(String.format("I cannot stop mashing in this state `%s`.", this.getClass()));
    }

    default BreweryState startSparging() {
        throw new RuntimeException(String.format("I cannot start sparging in this state `%s`.", this.getClass()));
    }

    default BreweryState stopSparging() {
        throw new RuntimeException(String.format("I cannot stop sparging in this state `%s`.", this.getClass()));
    }

    default BreweryState finish() {
        throw new RuntimeException(String.format("I cannot finish in this state `%s`.", this.getClass()));
    }

    double readTemperature();

    HeatingLevel readHeatingLevel();

}
