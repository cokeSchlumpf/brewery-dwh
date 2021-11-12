package systems.brewery.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import systems.brewery.model.instructions.Instruction;
import systems.reference.model.Employee;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Recipe {

    String beerKey;

    String beerName;

    Employee productOwner;

    Instant created;

    Instant updated;

    List<Instruction> instructions;

}
