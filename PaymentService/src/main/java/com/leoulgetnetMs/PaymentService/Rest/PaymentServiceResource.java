package com.leoulgetnetMs.PaymentService.Rest;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductInformationGrpc;
import com.google.protobuf.Int64Value;
import com.leoulgetnetMs.PaymentService.ExceptionHandler.InsufficientBalanceException;
import com.leoulgetnetMs.PaymentService.ExceptionHandler.ProductNotFoundException;
import com.leoulgetnetMs.PaymentService.Security.SecurityConfigGrpcTo.BearerTokenInterceptor;
import com.leoulgetnetMs.PaymentService.Security.SecurityConfigGrpcTo.TokenService;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/paymentservice")
public class PaymentServiceResource {


    private final KafkaTemplate<String,String> kafkaTemplate;
    private final TokenService tokenService;

    @GrpcClient("productInformation")
    private ProductInformationGrpc.ProductInformationBlockingStub productInformationBlockingStub;
    @GrpcClient("orderInformation")
    private LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.orderInformationGrpc.orderInformationBlockingStub orderInformationBlockingStub;


    @GetMapping("/{id}")
    ResponseEntity<String> payForOrder(@PathVariable("id") Long id) {
        try {
            String token = tokenService.getServiceToken();

            // Create authenticated gRPC stubs
            ProductInformationGrpc.ProductInformationBlockingStub productStub =
                    productInformationBlockingStub.withInterceptors(new BearerTokenInterceptor(token));

            LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.orderInformationGrpc.orderInformationBlockingStub orderStub =
                    orderInformationBlockingStub.withInterceptors(new BearerTokenInterceptor(token));

            // Get order details
            Int64Value orderRequest = Int64Value.newBuilder().setValue(id).build();
            LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.order order = orderStub.getOrderInformation(orderRequest);

            // Get product info for all items
            List<LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductRequest> productRequests =
                    order.getItemsList().stream()
                            .map(item -> LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductRequest.newBuilder()
                                    .setId(item.getProductId()).build())
                            .collect(Collectors.toList());

            List<LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse> productResponses =
                    productRequests.stream()
                            .map(productStub::getStockInformation)
                            .collect(Collectors.toList());

            // Validate stock for all items
            for (int i = 0; i < order.getItemsList().size(); i++) {
                LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.orderItems orderItem = order.getItemsList().get(i);
                LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse product = productResponses.get(i);

                if (product.getCurrentStock() < orderItem.getQuantity()) {
                    throw new InsufficientBalanceException(
                            "Not enough stock for product: " + orderItem.getProductId()
                    );
                }
            }

            // Deduct stock for all items
            for (int i = 0; i < order.getItemsList().size(); i++) {
                LeoulGetnetMs.OrderService.ProtoGenerated.OrderInfo.orderItems orderItem = order.getItemsList().get(i);
                LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse product = productResponses.get(i);

                productStub.deductStockRequest(
                        LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductDeductStockRequest.newBuilder()
                                .setId(product.getId())
                                .setQuantity(orderItem.getQuantity())
                                .build()
                );
            }
            // Update order status to PAID
            orderStub.changeOrderStatus(orderRequest);
            kafkaTemplate.send("ordercompletetion",String.valueOf(order.getOrderId()));

            return ResponseEntity.ok("Payment successful");

        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment failed: " + e.getMessage());
        }}





    @GetMapping("/check")
    ResponseEntity<String> payForOrder(){
    return ResponseEntity.ok("works");
    }}