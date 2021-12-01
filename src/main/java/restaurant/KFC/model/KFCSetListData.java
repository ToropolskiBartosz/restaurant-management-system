package restaurant.KFC.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class KFCSetListData {
    String name;
    String price;
    String mainProduct;
    String secondProduct;
    String drink;

    public KFCSetListData(String name, String price) {
        this.name = name;
        this.price= price;
    }
}
