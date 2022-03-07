package restaurant.KFC.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.KFC.Login.PasswordEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerData {

    private Long id;
    private String firstName;
    private String lastName;
    private String eMail;
    private String password;
    private String age;
    private String phoneNumber;
    private String city;
    private String adres;

    public Customer toCustomer(String role){
        var result = new Customer();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setAge(Integer.parseInt(age));
        result.setPhoneNumber(phoneNumber);
        result.setCity(city);
        result.setAdres(adres);
        result.seteMail(eMail);
        result.setPassword(PasswordEncoder.coding(password));
        result.setRole(role);
        return result;
    }
}
