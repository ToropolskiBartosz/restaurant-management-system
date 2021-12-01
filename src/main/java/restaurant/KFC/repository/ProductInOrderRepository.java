package restaurant.KFC.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.KFC.model.Product;
import restaurant.KFC.model.ProductInOrder;

import java.util.List;

public interface ProductInOrderRepository extends JpaRepository<ProductInOrder, Long> {
        List<ProductInOrder> findByOrderIdAndProductNotNull(long id);
        List<ProductInOrder> findByProductNotNull();
        List<ProductInOrder> findByOrderIdAndKfcSetNotNull(long id);
        List<ProductInOrder> findByKfcSetNotNull();
        List<ProductInOrder> findByOrderId(long id);

        @Query(value ="select ProductInOrder.kfcSet, sum(Product.kfcSet.price) " +
                "from KFCSet " +
                "group by Product.kfcSet", nativeQuery = true)
        List<String> findBySQLQuery();
}
