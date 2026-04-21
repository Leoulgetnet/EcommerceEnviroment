package LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Repository;
import LeoulGetnetMs.ProductService.ExceptionHandler.InsufficientBalanceException;
import LeoulGetnetMs.ProductService.ExceptionHandler.ProductNotFoundException;
import LeoulGetnetMs.ProductService.Product.Domain.Aggregiate.Product;
import LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Entity.ProductEntity;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class SpringDataProductRepository {
    private final JpaProductRepository productRepository;




    public List<Product> getAllProduct(){
        return productRepository.findAll().stream().map(ProductEntity::to).toList();}

    public Product getById(Long id){

        return ProductEntity.to(productRepository
                .findById(id).orElseThrow(()->new ProductNotFoundException()));}



    public Product save(Product product) throws InterruptedException {
        ProductEntity productEntity=ProductEntity.builder()
                .name(product.getName())
                .initialStock(product.getInitialStock())
                .currentStock(product.getInitialStock())
                .price(product.getPrice())
                .build();

        return ProductEntity.to(productRepository.save(productEntity));}




    public Product updateStock(int stock,Long productId){
        ProductEntity productEntity=productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException());
        if(!(productEntity.getCurrentStock()>=stock)){
            throw new InsufficientBalanceException();
        }
        productEntity.setCurrentStock(productEntity.getCurrentStock()-stock);
        System.out.println(stock + "" + productId);
        return ProductEntity.to(productRepository.save(productEntity)); }}