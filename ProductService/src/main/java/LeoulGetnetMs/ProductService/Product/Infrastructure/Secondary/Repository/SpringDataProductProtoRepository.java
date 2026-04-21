package LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Repository;


import LeoulGetnetMs.ProductService.ExceptionHandler.InsufficientBalanceException;
import LeoulGetnetMs.ProductService.Product.Domain.Aggregiate.Product;
import LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Entity.ProductEntity;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductDeductStockRequest;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductInformationGrpc;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductRequest;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@GrpcService
public class SpringDataProductProtoRepository extends ProductInformationGrpc.ProductInformationImplBase {
    private final SpringDataProductRepository productRepository;
    @Override
    public void getStockInformation(ProductRequest productRequest, StreamObserver<ProductResponse> productResponseStreamObserver){
        try{
        Product product=productRepository.getById(productRequest.getId());
        ProductResponse productResponse=ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setCurrentStock(product.getCurrentStock())
                .setPrice(product.getPrice().toString())
                .build();
        productResponseStreamObserver.onNext(productResponse);
        productResponseStreamObserver.onCompleted();}
     catch (Exception e) {
        // Send error back to client
        productResponseStreamObserver.onError(
                Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .augmentDescription("Additional Details "+e.getMessage())
                        .asRuntimeException()
        );
    }
    }

    @Override
    public void deductStockRequest(ProductDeductStockRequest stockRequest,
                                   StreamObserver<ProductResponse> streamObserver){
        try{
        Product product=productRepository.updateStock(stockRequest.getQuantity(),stockRequest.getId());
        ProductResponse productResponse=ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setCurrentStock(product.getCurrentStock())
                .setPrice(product.getPrice().toString()).build();
        streamObserver.onNext(productResponse);
        streamObserver.onCompleted();
        }
        catch (Exception e) {
            // Send error back to client
            streamObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .augmentDescription("Additional Details "+e.getMessage())
                            .asRuntimeException()
            );}}}
