package systems.reference;

import lombok.AllArgsConstructor;
import systems.reference.model.Employee;
import systems.reference.ports.ReferenceDataRepositoryPort;

import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class ReferenceDataManagement {

    private final ReferenceDataRepositoryPort repository;

    public void registerOrUpdateEmployee(Employee employee) {
        repository
            .findEmployeeById(employee.getId())
            .ifPresentOrElse(
                emp -> repository.updateEmployee(employee),
                () -> repository.insertEmployee(employee)
            );
    }

    public Optional<Employee> findEmployeeById(String id) {
        return repository.findEmployeeById(id);
    }

}
