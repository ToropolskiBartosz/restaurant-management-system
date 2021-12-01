package restaurant.KFC.Login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import restaurant.KFC.model.Customer;
import restaurant.KFC.repository.CustomerRepository;

import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<Customer> customer = customerRepository.findByeMail(s);
        System.out.println(customer.get(0).getFirstName());
        if(customer.size() == 0){
            throw new UsernameNotFoundException("Nie można znaleść klienta");
        }
        return new MyUserDetails(customer.get(0));
    }
}
