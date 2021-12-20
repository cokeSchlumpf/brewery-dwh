package systems.sales;

import systems.sales.values.Address;
import systems.sales.values.Customer;

import java.util.Optional;

public interface Customers {

    Optional<Customer> findCustomerById(int id);

    default Customer getCustomerById(int id) {
        return findCustomerById(id).orElseThrow();
    }

    Customer insertCustomer(String email, String firstname, String name, Address address);

}
