package simulation.entities.employee;

import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import simulation.entities.employee.messages.EmployeeMessage;
import systems.brewery.Brewery;
import systems.brewery.BreweryManagementSystem;

@Value
@AllArgsConstructor(staticName = "apply")
public class EmployeeContext {

    ActorContext<EmployeeMessage> actor;

    BreweryManagementSystem breweryManagementSystem;

    String name;

    public Brewery getBrewery() {
        return breweryManagementSystem.getBrewery();
    }

}
