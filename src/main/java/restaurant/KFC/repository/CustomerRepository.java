package restaurant.KFC.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.KFC.model.Customer;
import restaurant.KFC.model.Product;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
          List<Customer> findByeMail(String eMail);
          List<Customer> findByRole(String role);
}
