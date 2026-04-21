package LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Repository;

import LeoulGetnetMs.OrderService.ExceptionHandler.InsufficientBalanceException;
import LeoulGetnetMs.OrderService.ExceptionHandler.OrderNotFoundException;
import LeoulGetnetMs.OrderService.ExceptionHandler.ProductNotFoundException;
import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.Order;
import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.OrderItem;
import LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Entity.OrderEntity;
import LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Entity.OrderItemEntity;
import LeoulGetnetMs.OrderService.Security.SecurityConfigGrpcTo.BearerTokenInterceptor;
import LeoulGetnetMs.OrderService.Security.SecurityConfigGrpcTo.TokenService;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductInformationGrpc;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductRequest;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class SpringDataOrderRepository {

    private final TokenService tokenService;
    private final JpaOrderRepository orderRepository;

    @GrpcClient("productInformation")
    private ProductInformationGrpc.ProductInformationBlockingStub productInformationBlockingStub;



    public Order save(Order order) {
        // Get the service token
        String token = tokenService.getServiceToken();
        // Create stub with token interceptor
        ProductInformationGrpc.ProductInformationBlockingStub authStub =
                productInformationBlockingStub.withInterceptors(new BearerTokenInterceptor(token));

        List<ProductResponse> list;
        try {
            list = order.getItems().stream()
                    .map(e -> authStub.getStockInformation(
                            ProductRequest.newBuilder().setId(e.getProductId()).build()))
                    .toList();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Product service error: " + e.getStatus().getCode() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product information: " + e.getMessage(), e);
        }

        List<OrderItemEntity> OrderItemEntityList = new ArrayList<>();

        for (OrderItem o : order.getItems()) {
            Optional<ProductResponse> productResponse = list.stream()
                    .filter(e -> Long.valueOf(e.getId()).equals(o.getProductId()))
                    .findFirst();

            if (productResponse.isEmpty()) {
                throw new ProductNotFoundException("Product not found with ID: " + o.getProductId());
            }

            ProductResponse response = productResponse.get();

            if (o.getQuantity() > response.getCurrentStock()) {
                throw new InsufficientBalanceException("Insufficient stock for product: " + response.getName() + ". Available: " + response.getCurrentStock());
            }

            BigDecimal price = new BigDecimal(response.getPrice());

            OrderItemEntity orderItemEntity = OrderItemEntity.builder()
                    .productId(o.getProductId())
                    .productName(response.getName())
                    .quantity(o.getQuantity())
                    .price(price)
                    .subtotal(price.multiply(BigDecimal.valueOf(o.getQuantity())))
                    .build();

            OrderItemEntityList.add(orderItemEntity);
        }

        BigDecimal total = OrderItemEntityList.stream()
                .map(OrderItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity orderEntity = OrderEntity.builder()
                .items(OrderItemEntityList)
                .totalAmount(total)
                .status("PENDING")
                .build();

        return OrderEntity.to(orderRepository.save(orderEntity));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderEntity::to)
                .toList();
    }

    public Order getById(Long id) {
        return OrderEntity.to(orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException()));
    }




    public Order updateStatus(Long id, String status) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException());
        entity.setStatus(status);
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        return OrderEntity.to(orderRepository.save(entity));
    }}