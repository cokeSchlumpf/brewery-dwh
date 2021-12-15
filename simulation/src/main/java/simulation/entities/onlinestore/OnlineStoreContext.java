package simulation.entities.onlinestore;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.employee.messages.EmployeeMessage;
import simulation.entities.onlinestore.messages.OnlineStoreMessage;
import systems.sales.SalesManagementSystem;

@Value
@AllArgsConstructor(staticName = "apply")
public class OnlineStoreContext {
    private static final Logger LOG = LoggerFactory.getLogger(OnlineStore.class);

    ActorContext<OnlineStoreMessage> actor;

    SalesManagementSystem salesManagementSystem;

    ActorRef<EmployeeMessage> employee;

    String url;

    public void log(String message, Object...args) {
        LOG.info(String.format("%s -- %s -- %s", Clock.getInstance().getNow(), url, String.format(message, args)));
    }


}
