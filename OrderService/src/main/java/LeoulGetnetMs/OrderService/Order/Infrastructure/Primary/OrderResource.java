package LeoulGetnetMs.OrderService.Order.Infrastructure.Primary;

import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.Order;
import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.OrderItem;
import LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Repository.SpringDataOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderResource {

    private final SpringDataOrderRepository orderRepository;


    @KafkaListener(topics = "ordercompletetion",groupId = "ordercompletiongroupid")
    public void consumeEvent(String value){
        System.out.println("order completed for order id "+value);

    }
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderRepository.getById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<OrderItem> order) {
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderRepository.updateStatus(id, status));
    }
}