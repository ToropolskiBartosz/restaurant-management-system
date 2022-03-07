package restaurant.KFC.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.KFC.model.Customer;
import restaurant.KFC.model.CustomerData;
import restaurant.KFC.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void save (CustomerData source, String role){
        customerRepository.save(source.toCustomer(role));
    }

    public CustomerData getCustomer(Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    var result = new CustomerData();
                    result.setId(id);
                    result.setFirstName(customer.getFirstName());
                    result.setLastName(customer.getLastName());
                    result.setEMail(customer.geteMail());
                    result.setPassword(customer.getPassword());
                    result.setAge(Integer.toString(customer.getAge()));
                    result.setPhoneNumber(customer.getPhoneNumber());
                    result.setAdres(customer.getAdres());
                    return result;
                }).orElseThrow(() -> new IllegalArgumentException("Customer with give id not found"));
    }

    public void updateCustomer(CustomerData customerData) {
        var result = customerRepository.findById(customerData.getId())
                .map(customer -> {
                    customer.setFirstName(customerData.getFirstName());
                    customer.setLastName(customerData.getLastName());
                    customer.setAge(Integer.parseInt(customerData.getAge()));
                    customer.setPhoneNumber(customerData.getPhoneNumber());
                    customer.setCity(customerData.getCity());
                    customer.setAdres(customerData.getAdres());
                    return customer;
                }).orElseThrow(() -> new IllegalArgumentException("Customer with given id not found"));
        customerRepository.save(result);
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public void deleteById(long id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> getCustomerRoleUser() {
        return customerRepository.findByRole("ROLE_USER");
    }

    @Transactional
    public CustomerData getUserDate() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) user).getUsername();
        Customer customerLogin = customerRepository.findByeMail(username).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("customer with given username not found"));
        Customer userCustomer = customerRepository.findById(customerLogin.getId())
                .orElseThrow(() -> new IllegalArgumentException("customer with given id not found"));

        return new CustomerData(
                userCustomer.getId(),
                userCustomer.getFirstName(),
                userCustomer.getLastName(),
                userCustomer.geteMail(),
                userCustomer.getPassword(),
                Integer.toString(userCustomer.getAge()),
                userCustomer.getPhoneNumber(),
                userCustomer.getCity(),
                userCustomer.getAdres());
    }
}
