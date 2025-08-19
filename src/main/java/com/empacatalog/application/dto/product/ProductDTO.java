package com.empacatalog.application.dto.product;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para representar una respuesta de producto.
 * Utilizado para enviar datos de productos desde el servidor al cliente.
 */
@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String partNumber;
    private String category;
}
