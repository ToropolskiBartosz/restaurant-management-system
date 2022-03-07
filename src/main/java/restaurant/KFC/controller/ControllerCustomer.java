package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.KFC.Login.PasswordEncoder;
import restaurant.KFC.model.Customer;
import restaurant.KFC.model.CustomerData;
import restaurant.KFC.repository.CustomerRepository;
import restaurant.KFC.service.CustomerService;

import java.util.List;
import java.util.Optional;

@Controller
public class ControllerCustomer {
    CustomerService service;

    public ControllerCustomer(CustomerService service) {
        this.service = service;
    }

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
            service.save(customerData,"ROLE_USER");
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Wstawiono do bazy osobe: "
                    + customerData.getFirstName() +
                    " " + customerData.getLastName());
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak przy rejestracji");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/deleteCustomer/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable long id, Model model) {
        service.deleteById(id);
        model.addAttribute("header","Lista wszystkich klientów");
        model.addAttribute("customersList", getCustomer());
        return "customer_list";

    }

    @RequestMapping(value = "/editCustomer/{id}", method = RequestMethod.GET)
    public String editPerson(@PathVariable Long id,
                             Model model) {
        CustomerData customerData = service.getCustomer(id);
        model.addAttribute("customerData", customerData);

        return "customer_update";
    }

    @RequestMapping(value = "/editCustomer", method = RequestMethod.POST)
    public String editPerson(@ModelAttribute("customerData") CustomerData customerData, Model model)
    {
        try {
            service.updateCustomer(customerData);
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Edycja klienta o id:" + customerData.getId());
            return "viewmessage";
        }catch (IllegalArgumentException e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Nie odnaleziono tego klienta ");
            return "viewmessage";
        } catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... coś poszło nie tak" + e);
            return "viewmessage";
        }

    }

    @RequestMapping(value = "/print_all_customer", method = RequestMethod.GET)
    public String printAll(Model model) {
        try {
            model.addAttribute("header", "Lista wszystkich osob");
            model.addAttribute("customersList", service.getCustomerRoleUser());
            return "customer_list";
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups...");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/add_admin",method = RequestMethod.GET)
    public String addAdmin(Model model)
    {
        CustomerData customerData = new CustomerData();
        model.addAttribute("customerData", customerData);
        return "customer_registration_form";
    }

    @RequestMapping(value = "/add_admin", method = RequestMethod.POST)
    public String addAdmin(@ModelAttribute("customerData") CustomerData customerData,
                           Model model)
    {
        try {
            service.save(customerData, "ROLE_ADMIN");
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Wstawiono do bazy Admina: " + customerData.getFirstName()
                    + " " + customerData.getLastName());
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups.. coś poszło nie tak przy rejestracji");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.GET)
    public String editUser(Model model) {
        CustomerData customerData = service.getUserDate();
        model.addAttribute("customerData", customerData);
        return "customer_update";
    }

    @ModelAttribute("customersList")
    List<Customer> getCustomer(){
        return service.getAll();
    }
}
