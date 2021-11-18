package systems.reference.ports;

import common.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.reference.model.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class ReferenceDataRepositoryJdbcImpl implements ReferenceDataRepositoryPort {

    private final Jdbi jdbi;

    public static ReferenceDataRepositoryJdbcImpl apply(DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        return apply(jdbi);
    }

    @Override
    public void insertEmployee(Employee employee) {
        var query = "INSERT INTO sppl.MA_EMPLOYEES " +
            "(id, firstname, name, date_of_birth, position) VALUES " +
            "(:id, :firstname, :name, :date_of_birth, :position)";

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", employee.getId())
            .bind("firstname", employee.getFirstname())
            .bind("name", employee.getName())
            .bind("date_of_birth", employee.getDateOfBirth())
            .bind("position", employee.getPosition())
            .execute());
    }

    @Override
    public Optional<Employee> findEmployeeById(String id) {
        var query = "SELECT * FROM sppl.MA_EMPLOYEES " +
            "WHERE id = :id";

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id)
            .map(new EmployeeMapper())
            .stream()
            .findFirst());
    }

    @Override
    public void updateEmployee(Employee employee) {
        var query = "UPDATE sppl.MA_EMPLOYEES " +
            "SET firstname = :firstname, name = :name, date_of_birth = :date_of_birth, position = :position " +
            "WHERE id = :id";

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", employee.getId())
            .bind("firstname", employee.getFirstname())
            .bind("name", employee.getName())
            .bind("date_of_birth", employee.getDateOfBirth())
            .bind("position", employee.getPosition())
            .execute());
    }

    private static class EmployeeMapper implements RowMapper<Employee> {

        @Override
        public Employee map(ResultSet rs, StatementContext ctx) throws SQLException {
            return Employee.apply(
                rs.getString("id"),
                rs.getString("firstname"),
                rs.getString("name"),
                rs.getTimestamp("date_of_birth").toInstant(),
                rs.getString("position"));
        }

    }

}
