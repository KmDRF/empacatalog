package com.empacatalog.application.dto;

import com.empacatalog.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String partNumber;
    private String name;
    private String description;
    private String specifications;
    private String category;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;

    /**
     * Convierte una entidad Product a un DTO (Data Transfer Object).
     * Usa el patrón Builder para crear un nuevo DTO.
     * @param product La entidad Product para convertir.
     * @return Un nuevo objeto ProductDTO.
     */
    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDTO.builder()
                .id(product.getId())
                .partNumber(product.getPartNumber())
                .name(product.getName())
                .description(product.getDescription())
                .specifications(product.getSpecifications())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .build();
    }

    /**
     * Convierte este DTO de vuelta a una entidad Product.
     * @return Una nueva entidad Product.
     */
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setPartNumber(this.partNumber);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setSpecifications(this.specifications);
        product.setCategory(this.category);
        product.setPrice(this.price);
        product.setStockQuantity(this.stockQuantity);
        product.setImageUrl(this.imageUrl);
        return product;
    }
}
