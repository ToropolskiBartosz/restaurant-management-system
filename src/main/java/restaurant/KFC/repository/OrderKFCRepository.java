package restaurant.KFC.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.KFC.model.OrderKFC;
import restaurant.KFC.model.Product;

import java.util.List;

public interface OrderKFCRepository extends JpaRepository<OrderKFC, Long> {
    List<OrderKFC> findByFinishedAndCustomerId(boolean finished, long id);
    List<OrderKFC> findByFinished(boolean finished);
}
