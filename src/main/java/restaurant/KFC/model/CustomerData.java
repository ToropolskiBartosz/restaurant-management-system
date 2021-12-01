package restaurant.KFC.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerData {

    private String firstName;
    private String lastName;
    private String eMail;
    private String password;
    private String age;
    private String phoneNumber;
    private String city;
    private String adres;
}
