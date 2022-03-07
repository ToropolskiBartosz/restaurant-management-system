package restaurant.KFC.service;

import org.springframework.stereotype.Service;
import restaurant.KFC.model.Categories;
import restaurant.KFC.model.Product;
import restaurant.KFC.model.ProductData;
import restaurant.KFC.model.Size;
import restaurant.KFC.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void save(ProductData productData) {
        productRepository.save(productData.toProduct());
    }

    public ProductData getProduct(long id) {
        return productRepository.findById(id)
                .map(product -> {
                    var result = new ProductData();
                    result.setId(product.getId());
                    result.setName(product.getName());
                    result.setCategory(product.getCategory());
                    result.setPrice(Double.toString(product.getPrice()));
                    result.setSize(product.getSize());
                    result.setKcal(Integer.toString(product.getKcal()));
                    return result;
                })
                .orElseThrow(() -> new IllegalArgumentException("Product with given id not found"));
    }

    public void updateProduct(ProductData source) {
        var result = productRepository.findById(source.getId())
                .map(product -> {
                    product.setName(source.getName());
                    product.setCategory(source.getCategory());
                    product.setPrice(Double.parseDouble(source.getPrice()));
                    product.setSize(source.getSize());
                    product.setKcal(Integer.parseInt(source.getKcal()));
                    return product;
                }).orElseThrow(() -> new IllegalArgumentException("Product with given id not found"));
        productRepository.save(result);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<String> getCategories(){
        return Arrays.stream(Categories.values())
                .map(Categories::getDescription)
                .collect(Collectors.toList());
    }

    public Size[] getSize(){
        return Size.values();
    }
}
