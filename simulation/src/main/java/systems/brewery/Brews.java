package systems.brewery;

import systems.brewery.values.Bottling;
import systems.brewery.values.Brew;
import systems.brewery.values.event.BrewEvent;

import java.time.Instant;

public interface Brews {

    void insertBrew(Brew brew);

    void updateBrew(Instant finished, double finalGravity);

    void logBrewEvent(BrewEvent event);

    void logBottling(Bottling bottling);

    void clear();

}
