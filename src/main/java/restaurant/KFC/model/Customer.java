package restaurant.KFC.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotNull
    private String firstName;
    private String lastName;
    private int age;
    private String phoneNumber;
    private String city;
    private String adres;
    @Column(unique = true)
    private String eMail;
    private String password;
    private String role;
    private boolean enabled;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private Set<OrderKFC> order;

    protected Customer(){}

    public Customer( String firstName, String lastName,String eMail,String password, int age,String phoneNumber, String city,String adres){
        this.firstName = firstName;
        this.lastName =lastName;
        this.eMail=eMail;
        this.password=password;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.adres = adres;
        this.role = "ROLE_USER";
        this.enabled = true;
        order = new HashSet<OrderKFC>();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public Set<OrderKFC> getOrder() {
        return order;
    }

    public void setOrder(Set<OrderKFC> order) {
        this.order = order;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
