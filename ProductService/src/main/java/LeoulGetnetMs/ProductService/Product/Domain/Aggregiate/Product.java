package LeoulGetnetMs.ProductService.Product.Domain.Aggregiate;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class Product { /*Immutable so created by all args once and never change using setter*/
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

}
