package LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Repository;

import LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
}