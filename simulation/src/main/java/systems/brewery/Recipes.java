package systems.brewery;

import lombok.AllArgsConstructor;
import systems.brewery.values.Recipe;
import systems.brewery.values.instructions.Instruction;
import systems.reference.model.Employee;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class Recipes {

    public Recipe get(String beerKey) {
        throw new RuntimeException("not implemented");
    }

    public Optional<Recipe> find(String key) {
        throw new RuntimeException("not implemented");
    }

    public List<String> list() {
        throw new RuntimeException("not implemented");
    }

    public void remove(String beerKey) {
        throw new RuntimeException("not implemented");
    }

    public void register(String beerKey, String beerName, String productOwner, List<Instruction> instructions) {
        throw new RuntimeException("not implemented");
    }

    public void updateInstructions(String beerKey, List<Instruction> instructions) {
        throw new RuntimeException("not implemented");
    }

    public void updateName(String beerKey, String beerName) {
        throw new RuntimeException("not implemented");
    }

    public void updateProductOwner(Employee productOwner) {
        throw new RuntimeException("not implemented");
    }

}
