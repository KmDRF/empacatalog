package com.empacatalog.application.dto.pedido;

import com.empacatalog.application.dto.product.ProductResponse;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO para la respuesta de un ítem de pedido.
 * Incluye los detalles del producto, cantidad y precio unitario.
 */
@Getter
@Setter
public class PedidoItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
