package simulation.entities.employee;

import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.brewery.Brewery;
import simulation.entities.employee.messages.EmployeeMessage;
import systems.brewery.BreweryManagementSystem;

@Value
@AllArgsConstructor(staticName = "apply")
public class EmployeeContext {

    private static final Logger LOG = LoggerFactory.getLogger(Employee.class);

    ActorContext<EmployeeMessage> actor;

    BreweryManagementSystem breweryManagementSystem;

    Brewery brewery;

    systems.reference.model.Employee employee;

    public void log(String message, Object...args) {
        LOG.info(String.format("%s -- %s -- %s", Clock.getInstance().getNow(), employee.getId(), String.format(message, args)));
    }

}
