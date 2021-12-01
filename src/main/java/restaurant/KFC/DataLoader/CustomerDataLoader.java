package restaurant.KFC.DataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import restaurant.KFC.Login.PasswordEncoder;
import restaurant.KFC.model.Customer;
import restaurant.KFC.repository.CustomerRepository;
import restaurant.KFC.repository.ProductRepository;

@Component
public class CustomerDataLoader implements ApplicationRunner {
    private CustomerRepository customerRepository;

    @Autowired
    public CustomerDataLoader(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void run(ApplicationArguments args) {
        //Klienci
        customerRepository.save(new Customer("Jan","Kowalski",
                                        "JanKowalski@gmail.com", PasswordEncoder.coding("haslo"),
                                            22,"784321123",
                                            "Warszawa","Złote Tarasy 32"));

        Customer customer = new Customer("Bartosz","Toropolski",
                "bartosz0302@interia.pl",PasswordEncoder.coding("haslo"),
                23,"784621321",
                "Warszawa","Złote Tarasy 36");
        customer.setRole("ROLE_ADMIN");

        customerRepository.save(customer);

        customerRepository.save(new Customer("David","Stary",
                                                "davidStary@onet.pl",PasswordEncoder.coding("haslo"),
                                                25,"890312456",
                                            "Warszawa","Mokotów 136"));




    }
}

