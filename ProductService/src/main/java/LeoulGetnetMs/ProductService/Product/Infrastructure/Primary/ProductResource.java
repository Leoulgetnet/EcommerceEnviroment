package LeoulGetnetMs.ProductService.Product.Infrastructure.Primary;
import LeoulGetnetMs.ProductService.Product.Domain.Aggregiate.Product;
import LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Repository.SpringDataProductRepository;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductResource {
//    private final KafkaTemplate<String,byte[]> kafkaTemplate;
    private final SpringDataProductRepository productRepository;
    private int x=0;

    @CircuitBreaker(name="circuitBreakerService",fallbackMethod = "circuitBreakerCallBackMethod")
    @GetMapping("/circuitbreaker")
    ResponseEntity<?> checkingCircuitBreaker() throws InterruptedException {x++;
        if(x<5){
            throw new RuntimeException("Run Time Exception thrown");}
        return ResponseEntity.ok("it is working");}

    ResponseEntity<?> circuitBreakerCallBackMethod(Throwable throwable){
        return ResponseEntity.ok("callback method"+throwable.getMessage());}


    @GetMapping
    ResponseEntity<List<Product>> findAll() throws InterruptedException {
        return ResponseEntity.ok(productRepository.getAllProduct());
    }


//    @Retry(name="retryFindByid",fallbackMethod = "findByIdFBM")
    @GetMapping("/{id}")
    ResponseEntity<Product> findById(@PathVariable("id") Long id){
        Product product=productRepository.getById(id);
        ProductResponse productResponse=ProductResponse.newBuilder()
                .setId(12)
                .setPrice(BigDecimal.valueOf(1000).toString()).setCurrentStock(1)
                .setName("Shoes")
                .build();
//        kafkaTemplate.send("testActions", productResponse.toByteArray());
            return ResponseEntity.ok(product);
      }


//    ResponseEntity<String> findByIdFBM(Long id,Exception e){
//        return ResponseEntity.ok().body("There is no product with this id!");
//    }



    @PostMapping
    ResponseEntity<Product> saveProduct(@RequestBody Product product) throws InterruptedException {
        System.out.print(product);
        return ResponseEntity.ok(productRepository.save(product));
    }



    @Retry(name="updateStockRetry" , fallbackMethod = "updateStockFBM")
    @PutMapping("/{id}")
    ResponseEntity<Product> updateStock(@RequestParam("amount") int stockAmount,@PathVariable Long id){
       return ResponseEntity.ok(productRepository.updateStock(stockAmount, id));}

    ResponseEntity<String> updateStockFBM(){
        return ResponseEntity.ok().body("Product Must Be Out Of Stock!");
    }
}
