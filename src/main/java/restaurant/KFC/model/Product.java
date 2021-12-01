package restaurant.KFC.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;
    private String category;
    private double price;
    private String size;
    private int kcal;

    @ManyToMany(mappedBy = "product",fetch = FetchType.EAGER)
    private Set<KFCSet> kfcSet;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<ProductInOrder> productInOrder;

    protected Product(){}

    public Product(java.lang.String name, java.lang.String category, double price, java.lang.String size, int kcal) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.size = size;
        this.kcal = kcal;
        kfcSet=new HashSet<KFCSet>();
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public Set<KFCSet> getKfcSet() {
        return kfcSet;
    }

    public void setKfcSet(Set<KFCSet> kfcSet) {
        this.kfcSet = kfcSet;
    }

    public Set<ProductInOrder> getProductInOrder() {
        return productInOrder;
    }

    public void setProductInOrder(Set<ProductInOrder> productInOrder) {
        this.productInOrder = productInOrder;
    }

    public void removeKFCSetFromProduct(KFCSet kfcSet) {
        this.getKfcSet().remove(kfcSet);
    }
}


