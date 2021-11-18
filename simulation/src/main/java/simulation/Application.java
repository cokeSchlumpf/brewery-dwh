package simulation;

import akka.actor.typed.ActorSystem;
import common.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import systems.reference.ReferenceDataManagement;
import systems.reference.model.Employee;
import systems.reference.ports.ReferenceDataRepositoryJdbcImpl;

import java.time.Instant;

public final class Application {

    private static Logger LOG = LoggerFactory.getLogger("sim");

    public static void main(String... args) {
        var johnny = Employee.apply("johnny", "Johnny", "Goldgreen", Instant.now(), "Founder 1");
        var mike = Employee.apply("mike", "Mike", "Hemington", Instant.now(), "Founder");

        var databaseConfig = DatabaseConfiguration.apply("jdbc:postgresql://localhost:5432/brewery", "postgres", "password");
        var refDataRepo = ReferenceDataRepositoryJdbcImpl.apply(databaseConfig);
        var refDataMgmt = ReferenceDataManagement.apply(refDataRepo);

        refDataMgmt.registerOrUpdateEmployee(johnny);
        refDataMgmt.registerOrUpdateEmployee(mike);

        System.out.println(refDataMgmt.findEmployeeById("johnny"));

        ActorSystem.create(World.create(), "world");
    }

}
