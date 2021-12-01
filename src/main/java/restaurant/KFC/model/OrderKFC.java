package restaurant.KFC.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class OrderKFC {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String date;
    private boolean finished;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<ProductInOrder> productInOrder;

    private String phoneNumber;
    private String city;
    private String adres;

    protected OrderKFC(){}

    public OrderKFC( String date, boolean finished) {
        customer = null;
        this.date = date;
        this.finished = finished;
        productInOrder = new HashSet<ProductInOrder>();
        phoneNumber = null;
        city = null;
        adres = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<ProductInOrder> getProductInOrder() {
        return productInOrder;
    }

    public void setProductInOrder(Set<ProductInOrder> productInOrder) {
        this.productInOrder = productInOrder;
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
}
