package com.empacatalog.application.dto.product;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO para la respuesta de un producto.
 * Muestra solo la información relevante del producto.
 */
@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String partNumber;
    private String category;
}
