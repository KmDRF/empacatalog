package com.empacatalog.application.dto.product;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO para la creación de un nuevo producto.
 */
@Getter
@Setter
public class ProductCreationRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String partNumber;
    private String category;
    private boolean active;
}
