package restaurant.KFC.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class KFCSet {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;
    private double price;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Product> product;

    @OneToMany(mappedBy = "kfcSet",fetch = FetchType.EAGER)
    private Set<ProductInOrder> productInOrder;

    protected KFCSet(){}

    public KFCSet(String name, double price) {
        this.name= name;
        this.price = price;
        product = new HashSet<Product>();

        productInOrder = new HashSet<ProductInOrder>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<ProductInOrder> getProductInOrder() {
        return productInOrder;
    }

    public void setProductInOrder(Set<ProductInOrder> productInOrder) {
        this.productInOrder = productInOrder;
    }

    public Set<Product> getProduct() {
        return product;
    }

    public void setProduct(Set<Product> product) {
        this.product = product;
    }

    public void addProduct(Product product){
        this.getProduct().add(product);
        product.getKfcSet().add(this);
    }
}
