package simulation.entities.customer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.customer.messages.CustomerMessage;
import simulation.entities.customer.values.CustomerType;
import simulation.entities.employee.Employee;
import simulation.entities.employee.messages.EmployeeMessage;

import java.util.List;


@Value
@AllArgsConstructor(staticName = "apply")
public class CustomerContext {

    private static final Logger LOG = LoggerFactory.getLogger(Customer.class);

    ActorContext<CustomerMessage> actor;

    ActorRef<EmployeeMessage> employee;

    List<String> favoriteBeers;

    CustomerType customerType;

    String name;

    // zukünftig noch Datenbankobjekt

    public void log(String message, Object...args) {
        LOG.info(String.format("%s -- %s -- %s", Clock.getInstance().getNow(), this.name,String.format(message, args)));
    }

}
