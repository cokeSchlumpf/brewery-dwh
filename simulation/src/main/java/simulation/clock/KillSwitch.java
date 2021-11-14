package simulation.clock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class KillSwitch {

    private boolean killed;

    public static KillSwitch apply() {
        return new KillSwitch(false);
    }

    public void kill() {
        this.killed = true;
    }

    public boolean isKilled() {
        return killed;
    }

}
