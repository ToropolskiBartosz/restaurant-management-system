package restaurant.KFC.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductData {

    private long id;
    private String name;
    private String category;
    private String price;
    private String size;
    private String kcal;

    public Product toProduct(){
        var result = new Product();
        result.setName(name);
        result.setCategory(category);
        result.setPrice(Double.parseDouble(price));
        result.setSize(size);
        result.setKcal(Integer.parseInt(kcal));
        return result;
    }
}
