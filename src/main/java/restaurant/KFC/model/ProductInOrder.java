package restaurant.KFC.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProductInOrder {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

     //private int amount;

    @ManyToOne
    private OrderKFC order;

    @ManyToOne
    private Product product;

    @ManyToOne
    private KFCSet kfcSet;

    //protected ProductInOrder(){}

    public ProductInOrder() {
        order = null;
        product = null;
        kfcSet = null;
        //this.amount = amount;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public int getAmount() {
//        return amount;
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public KFCSet getKfcSet() {
        return kfcSet;
    }

    public void setKfcSet(KFCSet kfcSet) {
        this.kfcSet = kfcSet;
    }

    public OrderKFC getOrder() {
        return order;
    }

    public void setOrder(OrderKFC order) {
        this.order = order;
    }
}
