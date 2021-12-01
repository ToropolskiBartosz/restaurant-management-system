package restaurant.KFC.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurant.KFC.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
            List<Product> findByKfcSetName(String name);
            List<Product>  findByKfcSetId(long id);
            List<Product> findByCategory(String name);
            List<Product> findByCategoryAndKfcSetName(String name,String nameSet);
            List<Product> findByProductInOrderOrderId(long id);
            List<Product> findByNameAndSize(String name, String size);
}
