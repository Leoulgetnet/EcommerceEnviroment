package LeoulGetnetMs.ProductService.Product.Infrastructure.Secondary.Entity;

import LeoulGetnetMs.ProductService.Product.Domain.Aggregiate.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Builder
@Table(name="product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    /*Identity:the database assigns sequential numbers by itself .
              :only after id
    * Sequence:There is another object assigning numbers not the Product Entity class unlike of Identity.
    *         : you can get id before insert
    *           @SequenceGenerator(name = "seq", sequenceName = "product_seq")
    *           @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    *           private Long id;
    * ! Both numbers are sequential.
    */

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    @Min(0)
    private int initialStock;
    @NotNull
    @Min(0)
    private int currentStock;
    @NotNull
    @Min(0)
    private BigDecimal price;

    public static Product to(ProductEntity productEntity){
        return Product.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .initialStock(productEntity.getInitialStock())
                .currentStock(productEntity.getCurrentStock())
                .price(productEntity.getPrice())
                .build();}

    public static ProductEntity from(Product product){
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .initialStock(product.getInitialStock())
                .currentStock(product.getInitialStock())
                .price(product.getPrice())
                .build();}}
