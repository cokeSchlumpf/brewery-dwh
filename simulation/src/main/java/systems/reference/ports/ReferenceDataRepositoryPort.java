package systems.reference.ports;

import systems.reference.model.Employee;

import java.util.Optional;

public interface ReferenceDataRepositoryPort {

    void insertEmployee(Employee employee);

    Optional<Employee> findEmployeeById(String id);

    void updateEmployee(Employee employee);
}
