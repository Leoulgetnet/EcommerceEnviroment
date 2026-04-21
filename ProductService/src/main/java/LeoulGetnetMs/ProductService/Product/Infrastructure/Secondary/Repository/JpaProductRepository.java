package LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Repository;

import LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity,Long> {
}
