package simulation;

import akka.actor.typed.ActorSystem;
import common.configs.ApplicationConfiguration;
import simulation.clock.Clock;
import simulation.entities.brewery.Brewery;
import systems.brewery.BreweryManagementSystem;
import systems.reference.ReferenceDataManagement;
import systems.reference.ports.ReferenceDataRepositoryJdbcImpl;

import java.time.Duration;

public final class Application {

    public static void main(String... args) {
        var config = ApplicationConfiguration.apply();
        var databaseConfig = config.getDatabase();
        var refDataRepo = ReferenceDataRepositoryJdbcImpl.apply(databaseConfig);
        var refDataMgmt = ReferenceDataManagement.apply(refDataRepo);

        var bms = BreweryManagementSystem.apply(databaseConfig);
        var brewery = Brewery.apply();

        /*
         * Prepare initial data
         */
        databaseConfig.migrate();

        /*
         * Initialize simulation.
         */
        var system = ActorSystem.create(World.create(refDataMgmt, bms, brewery), "world");
        var killSwitch = Clock.getInstance().run();

        Clock.getInstance().startSingleTimer("kill", Duration.ofDays(365), () -> {
            system.terminate();
            killSwitch.kill();
        });
    }

}
