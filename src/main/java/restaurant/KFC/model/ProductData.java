package restaurant.KFC.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductData {

    private String name;
    private String category;
    private String price;
    private String size;
    private String kcal;
}
