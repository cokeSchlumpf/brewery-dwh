package systems.sales;

import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import systems.sales.values.Address;
import systems.sales.values.Customer;

import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class CustomersJdbcImpl implements Customers {

    private final Jdbi jdbi;


    @Override
    public Optional<Customer> findCustomerById(int id) {
        return Optional.empty();
    }

    @Override
    public Customer insertCustomer(String email, String firstname, String name, Address address) {
        return null;
    }

}
