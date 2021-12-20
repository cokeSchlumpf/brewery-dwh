package simulation.entities.customer.behaviors;

import akka.japi.Pair;
import common.ProbabilityDistribution;
import systems.brewery.values.Recipe;

import java.util.List;

public final class CustomerBehaviors {

    private CustomerBehaviors() {

    }

    public static CustomerBehavior createSam() {
        return StudentBehavior.apply(
            CustomerProperties.apply(
                ProbabilityDistribution.apply(Pair.apply(0.5D, "sam.hensington@gmail.com"), Pair.apply(0.3D, "sam.hensington@googlemail.com"), Pair.apply(0.2D, "samhensington@gmail.com")),
                ProbabilityDistribution.singleValue("Sam"),
                ProbabilityDistribution.singleValueWithPotentialTypos("Hensington"),
                ProbabilityDistribution.singleValueWithPotentialTypos("Christmas Street 42"),
                ProbabilityDistribution.singleValue("12345"),
                ProbabilityDistribution.singleValueWithPotentialTypos("München")),
            List.of(Recipe.barBeer().getBeerName()));
    }

    public static CustomerBehavior createOlga() {
        return PartyPlannerBehavior.apply(
            CustomerProperties.apply(
                ProbabilityDistribution.singleValue("olga.kornikaova@mail.ru"),
                ProbabilityDistribution.singleValue("Olga"),
                ProbabilityDistribution.singleValueWithPotentialTypos("Kornikova"),
                ProbabilityDistribution.singleValueWithPotentialTypos("Hirschblüten-Weg 3"),
                ProbabilityDistribution.singleValue("12345"),
                ProbabilityDistribution.singleValueWithPotentialTypos("München")));
    }

}
