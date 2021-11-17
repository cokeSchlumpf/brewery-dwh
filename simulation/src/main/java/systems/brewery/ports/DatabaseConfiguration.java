package systems.brewery.ports;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatabaseConfiguration {

    String connection;

    String username;

    String password;

}
