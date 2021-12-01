package restaurant.KFC.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.KFC.model.Customer;
import restaurant.KFC.model.KFCSet;
import restaurant.KFC.model.Product;

import java.util.List;

public interface KFC_SetRepository extends JpaRepository<KFCSet, Long> {
        List<KFCSet> findByProductInOrderOrderId(long id);

        @Query(value ="select KFCSet.name,KFCSet.price " +
                      "from KFCSet " +
                      "group by KFCSet.name,KFCSet.price", nativeQuery = true)
        List<String> findBySQLQuery();

        @Query(value ="select s.product from KFCSet", nativeQuery = true)
        List<String> findKFCSet();

        List<KFCSet> findByProductName(String productName);

}
