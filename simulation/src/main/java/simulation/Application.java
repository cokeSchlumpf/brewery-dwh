package simulation;

import akka.actor.typed.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application {

    private static Logger LOG = LoggerFactory.getLogger("sim");

    public static void main(String... args) {
        ActorSystem.create(World.create(), "world");
    }

}
