package com.empacatalog.application.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean active;
    private Integer stock; // <-- NUEVO CAMPO
}
