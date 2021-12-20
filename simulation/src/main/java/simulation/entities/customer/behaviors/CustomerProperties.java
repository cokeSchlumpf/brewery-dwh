package simulation.entities.customer.behaviors;

import common.ProbabilityDistribution;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class CustomerProperties {

    ProbabilityDistribution<String> email;

    ProbabilityDistribution<String> firstName;

    ProbabilityDistribution<String> lastName;

    ProbabilityDistribution<String> street;

    ProbabilityDistribution<String> zipCode;

    ProbabilityDistribution<String> city;

}
