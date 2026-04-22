package LeoulGetnetMs.OrderService.Order.Domain.Aggregiate;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class Order {
    private Long id;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;  // PENDING, PAID, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

