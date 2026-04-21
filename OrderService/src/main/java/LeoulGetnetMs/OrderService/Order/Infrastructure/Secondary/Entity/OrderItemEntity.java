package LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Entity;

import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.OrderItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    public static OrderItem to(OrderItemEntity entity) {
        if (entity == null) return null;

        return OrderItem.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .subtotal(entity.getSubtotal())
                .build();
    }

    public static OrderItemEntity from(OrderItem item) {
        if (item == null) return null;

        return OrderItemEntity.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}