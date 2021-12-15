package simulation.entities.onlinestore.state;

import lombok.AllArgsConstructor;
import simulation.entities.onlinestore.OnlineStoreContext;

@AllArgsConstructor(staticName = "apply")
public class IdleState implements State{

    private final OnlineStoreContext ctx;

}
