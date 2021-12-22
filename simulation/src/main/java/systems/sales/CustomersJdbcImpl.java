package systems.sales;

import common.Templates;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import systems.sales.values.Address;
import systems.sales.values.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class CustomersJdbcImpl implements Customers {

    private final Jdbi jdbi;


    @Override
    public Optional<Customer> findCustomerById(int id) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/customers--select-by-id.sql");

        return jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id)
            .map(CustomerMapper.apply())
            .stream()
            .findFirst());
    }

    @Override
    public Customer insertCustomer(String email, String firstname, String name, Address address) {
        var query = Templates.renderTemplateFromResources("db/sql/sales/customers--insert.sql");

        return jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("email", email)
            .bind("firstname", firstname)
            .bind("name", name)
            .bind("street", address.getStreet())
            .bind("zip_code", address.getZipCode())
            .bind("city", address.getCity())
            .executeAndReturnGeneratedKeys("id", "email", "firstname", "name", "street", "zip_code", "city")
            .map(CustomerMapper.apply())
            .first());
    }

    @Override
    public void clear() {
        var query = Templates.renderTemplateFromResources("db/sql/sales/customers--delete.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .execute());
    }

    @AllArgsConstructor(staticName = "apply")
    private static class CustomerMapper implements RowMapper<Customer> {

        @Override
        public Customer map(ResultSet rs, StatementContext ctx) throws SQLException {
            return Customer.apply(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("firstname"),
                rs.getString("name"),
                Address.apply(
                    rs.getString("street"),
                    rs.getString("zip_code"),
                    rs.getString("city")
                ));
        }

    }

}
