package simulation;

import akka.actor.typed.ActorSystem;
import common.configs.ApplicationConfiguration;
import simulation.clock.Clock;
import simulation.entities.brewery.Brewery;
import systems.brewery.BreweryManagementSystem;
import systems.reference.ReferenceDataManagement;
import systems.reference.ports.ReferenceDataRepositoryJdbcImpl;
import systems.sales.SalesManagementSystem;

import java.time.Duration;

public final class Application {

    public static void main(String... args) {
        var config = ApplicationConfiguration.apply();
        var databaseConfig = config.getDatabase();
        var refDataRepo = ReferenceDataRepositoryJdbcImpl.apply(databaseConfig);
        var refDataMgmt = ReferenceDataManagement.apply(refDataRepo);

        var bms = BreweryManagementSystem.apply(databaseConfig);
        var brewery = Brewery.apply();

        var sms = SalesManagementSystem.apply(databaseConfig);

        /*
         * Prepare initial data
         */
        databaseConfig.migrate();

        /*
         * Initialize simulation.
         */
        Clock.getInstance().startPeriodicTimer("Say time", Duration.ofDays(1), () -> {
            System.out.println("---");
        });

        var system = ActorSystem.create(World.create(refDataMgmt, bms, brewery, sms), "world");
        var killSwitch = Clock.getInstance().run();

        Clock.getInstance().startSingleTimer("kill", Duration.ofDays(365), () -> {
            system.terminate();
            killSwitch.kill();
        });
    }

}
