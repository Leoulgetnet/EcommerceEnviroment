package LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Repository;

import LeoulGetnetMs.OrderService.ExceptionHandler.OrderNotFoundException;
import LeoulGetnetMs.OrderService.Order.Domain.Aggregiate.Order;
import LeoulGetnetMs.OrderService.Order.Infrastructure.Secondary.Entity.OrderEntity;
import LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.order;
import LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.orderInformationGrpc;
import LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.orderItems;
import com.google.protobuf.Int64Value;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@GrpcService
@Repository
@RequiredArgsConstructor
@Slf4j
public class SpringDataProtoOrderRepository extends orderInformationGrpc.orderInformationImplBase {
    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public void getOrderInformation(Int64Value request, StreamObserver<order> streamObserver) {
        // Add validation for missing request value
        if (request.getValue()==0) {
            streamObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Order ID is required")
                            .asRuntimeException()
            );
            return;
        }

        try {
            OrderEntity orderEntity = this.getOrderEntity(request.getValue());
            List<orderItems> items = orderEntity.getItems().stream()
                    .map(e -> orderItems.newBuilder()
                            .setQuantity(e.getQuantity())
                            .setProductId(e.getProductId())
                            .build())
                    .toList();

            order order = LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.order.newBuilder()
                    .setOrderId(orderEntity.getId())
                    .addAllItems(items)
                    .build();

            streamObserver.onNext(order);
            streamObserver.onCompleted();

        } catch (OrderNotFoundException e) {
            log.error("Order not found: {}", request.getValue(), e);
            throw Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException();
        } catch (Exception e) {
            log.error("Unexpected error: {}", request.getValue(), e);
            throw Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException();
        }
    }

    @Override
    public void changeOrderStatus(Int64Value request, StreamObserver<order> streamObserver) {


        // Add validation for missing request value
        if (request.getValue()==0) {
            streamObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Order ID is required")
                            .asRuntimeException()
            );
            return;
        }

        try {
            OrderEntity orderEntity = this.getOrderEntity(request.getValue());
            orderEntity.setStatus("COMPLETED");
            OrderEntity savedEntity = jpaOrderRepository.save(orderEntity);

            // Build and return the updated order with items
            List<orderItems> items = savedEntity.getItems().stream()
                    .map(e -> orderItems.newBuilder()
                            .setQuantity(e.getQuantity())
                            .setProductId(e.getProductId())
                            .build())
                    .toList();

            order updatedOrder = LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.order.newBuilder()
                    .setOrderId(savedEntity.getId())
                    .addAllItems(items)
                    .build();

            streamObserver.onNext(updatedOrder);
            streamObserver.onCompleted();

        } catch (OrderNotFoundException e) {
            log.error("Order not found for status change: {}", request.getValue(), e);
            throw Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException();
        } catch (Exception e) {
            log.error("Unexpected error changing status: {}", request.getValue(), e);
            throw Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException();
        }
    }

    public OrderEntity getOrderEntity(Long id) {
        Optional<OrderEntity> orderEntity = jpaOrderRepository.findById(id);
        if (orderEntity.isEmpty()) {
            throw new OrderNotFoundException();
        }
        return orderEntity.get();
    }
}