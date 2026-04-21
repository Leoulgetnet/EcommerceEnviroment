package LeoulGetnetMs.OrderService.Order.Domain.Aggregiate;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Builder
@Getter
public class OrderItem {
    private Long productId;
    private String productName;  // Snapshot from Product Service (will come via gRPC)
    private Integer quantity;
    private BigDecimal price;     // Snapshot from Product Service (will come via gRPC)
    private BigDecimal subtotal;  // price * quantity
}