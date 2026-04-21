package LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Entity;

import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items = new ArrayList<>();

    private BigDecimal totalAmount;
    private String status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Order to(OrderEntity entity) {
        if (entity == null) return null;

        return Order.builder()
                .id(entity.getId())
                .items(entity.getItems() != null ?
                        entity.getItems().stream().map(OrderItemEntity::to).toList() : null)
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static OrderEntity from(Order order) {
        if (order == null) return null;

        return OrderEntity.builder()
                .id(order.getId())
                .items(order.getItems() != null ?
                        order.getItems().stream().map(OrderItemEntity::from).toList() : null)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .build();
    }
}