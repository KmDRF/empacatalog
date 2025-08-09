package com.empacatalog.application.dto.product;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO para la actualización de un producto existente.
 */
@Getter
@Setter
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean active;
}
