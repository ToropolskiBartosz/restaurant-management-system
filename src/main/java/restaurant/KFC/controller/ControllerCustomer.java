package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.KFC.Login.PasswordEncoder;
import restaurant.KFC.model.Customer;
import restaurant.KFC.model.CustomerData;
import restaurant.KFC.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;

@Controller
public class ControllerCustomer {

    @Autowired
    CustomerRepository customerRepository;

    //------------DODAWANIE KLIENTA
    @RequestMapping(value = "/add_customer",method = RequestMethod.GET)
    public String addPerson(Model model)
    {
        CustomerData customerData = new CustomerData();
        model.addAttribute("customerData", customerData);
        return "customer_registration_form";
    }

    @RequestMapping(value = "/add_customer", method = RequestMethod.POST)
    public String addPerson(Model model,CustomerData customerData)
    {
        try {
            String firstName = customerData.getFirstName();
            String lastName = customerData.getLastName();
            int age = Integer.parseInt(customerData.getAge());
            String phoneNumber = customerData.getPhoneNumber();
            String city = customerData.getCity();
            String adres = customerData.getAdres();
            String eMail = customerData.getEMail();
            String password = PasswordEncoder.coding(customerData.getPassword());

            customerRepository.save(new Customer(firstName, lastName, eMail, password, age, phoneNumber, city, adres));

            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Wstawiono do bazy osobe: " + firstName + " " + lastName);
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak przy rejestracji");
            return "viewmessage";
        }
    }

    //---------

    //----------USUWANIE KLIENTA
    @RequestMapping(value = "/deleteCustomer", method = RequestMethod.GET)
    public String deleteUser(@RequestParam(name = "customerId")String id, Model model) {


        Long longId = Long.parseLong(id);
        customerRepository.deleteById(longId);
        List<Customer> customersList =  customerRepository.findAll();
        model.addAttribute("header","Lista wszystkich klientów");
        model.addAttribute("customersList", customersList);
        return "customer_list";
    }
    //-------------------------

    //----------- EDYTOWANIE KLIENTA
    @RequestMapping(value = "/editCustomer", method = RequestMethod.GET)
    public String editPerson(@RequestParam(name = "customerId")String id,
                             @RequestParam(name = "customerFirstName")String firstName,
                             @RequestParam(name = "customerLastName")String lastName,
                             @RequestParam(name = "customerAge")String age,
                             @RequestParam(name = "customerPhoneNumber")String phoneNumber,
                             @RequestParam(name = "customerCity")String city,
                             @RequestParam(name = "customerAdres")String adres,
                             @RequestParam(name = "customerEmail")String eMail,
                             @RequestParam(name = "customerPassword")String password,
                             Model model) {

        CustomerData customerData = new CustomerData(firstName, lastName,eMail,password, age, phoneNumber, city,adres);
        model.addAttribute("customerData", customerData);

        return "customer_registration_form";
    }

    @RequestMapping(value = "/editCustomer", method = RequestMethod.POST)
    public String editPerson(@RequestParam(name = "customerId")String id, Model model, CustomerData customerData)
    {
        try {
            Long customerId = Long.parseLong(id);
            Optional<Customer> customer = customerRepository.findById(customerId);

            String firstName = customerData.getFirstName();
            String lastName = customerData.getLastName();
            int age = Integer.parseInt(customerData.getAge());
            String phoneNumber = customerData.getPhoneNumber();
            String city = customerData.getCity();
            String adres = customerData.getAdres();

            if (customer.isPresent()) {
                customer.get().setFirstName(firstName);
                customer.get().setLastName(lastName);
                customer.get().setAge(age);
                customer.get().setPhoneNumber(phoneNumber);
                customer.get().setCity(city);
                customer.get().setAdres(adres);
                customerRepository.save(customer.get());
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Edycja klienta o id:" + customerId);
                return "viewmessage";
            } else {
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Nie odnaleziono klienta o taki id:");
                return "viewmessage";
            }
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... coś poszło nie tak");
            return "viewmessage";
        }

    }

    //--------------------

    @RequestMapping(value = "/print_all_customer", method = RequestMethod.GET)
    public String printAll(Model model)
    {
        try {
            List<Customer> customersList = customerRepository.findByRole("ROLE_USER");
            model.addAttribute("header", "Lista wszystkich osob"); //Dodanie obiektu do pamieci lokalnej modelu
            model.addAttribute("customersList", customersList); //Dodanie obiektu do pamieci lokalnej modelu

            return "customer_list"; //Przekierowanie na strone
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak przy rejestracji");
            return "viewmessage";
        }
    }

    //------------DODAWANIE KLIENTA
    @RequestMapping(value = "/add_admin",method = RequestMethod.GET)
    public String addAdmin(Model model)
    {
        CustomerData customerData = new CustomerData();
        model.addAttribute("customerData", customerData);
        return "customer_registration_form";
    }

    @RequestMapping(value = "/add_admin", method = RequestMethod.POST)
    public String addAdmin(Model model,CustomerData customerData)
    {
        try {
            String firstName = customerData.getFirstName();
            String lastName = customerData.getLastName();
            int age = Integer.parseInt(customerData.getAge());
            String phoneNumber = customerData.getPhoneNumber();
            String city = customerData.getCity();
            String adres = customerData.getAdres();
            String eMail = customerData.getEMail();
            String password = PasswordEncoder.coding(customerData.getPassword());
            Customer admin = new Customer(firstName, lastName, eMail, password, age, phoneNumber, city, adres);
            admin.setRole("ROLE_ADMIN");
            customerRepository.save(admin);

            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Wstawiono do bazy Admina: " + firstName + " " + lastName);
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups.. coś poszło nie tak przy rejestracji");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.GET)
    public String editUser(Model model) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) user).getUsername();
        List<Customer> customerLogin = customerRepository.findByeMail(username);
        Optional<Customer> userCustomer = customerRepository.findById(customerLogin.get(0).getId());

        CustomerData customerData = new CustomerData(userCustomer.get().getFirstName(),
                userCustomer.get().getLastName(),
                userCustomer.get().geteMail(),
                userCustomer.get().getPassword(),
                Integer.toString(userCustomer.get().getAge()),
                userCustomer.get().getPhoneNumber(),
                userCustomer.get().getCity(),
                userCustomer.get().getAdres());
        model.addAttribute("customerData", customerData);

        return "customer_registration_form";
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public String editUser( Model model, CustomerData customerData)
    {
        try {
            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) user).getUsername();
            List<Customer> customerLogin = customerRepository.findByeMail(username);
            Optional<Customer> customer = customerRepository.findById(customerLogin.get(0).getId());

            String firstName = customerData.getFirstName();
            String lastName = customerData.getLastName();
            String email = customerData.getEMail();
            String password = customerData.getPassword();
            int age = Integer.parseInt(customerData.getAge());
            String phoneNumber = customerData.getPhoneNumber();
            String city = customerData.getCity();
            String adres = customerData.getAdres();

            if (customer.isPresent()) {
                customer.get().setFirstName(firstName);
                customer.get().setLastName(lastName);
                customer.get().seteMail(email);
                customer.get().setPassword(PasswordEncoder.coding(password));
                customer.get().setAge(age);
                customer.get().setPhoneNumber(phoneNumber);
                customer.get().setCity(city);
                customer.get().setAdres(adres);
                customerRepository.save(customer.get());
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Edycja uzytkownika: " + customer.get().geteMail());
                return "viewmessage";
            } else {
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Nie odnaleziono klienta o taki id:");
                return "viewmessage";
            }
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... coś poszło nie tak");
            return "viewmessage";
        }
    }
}
