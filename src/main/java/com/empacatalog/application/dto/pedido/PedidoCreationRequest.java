package com.empacatalog.application.dto.pedido;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la creación de un nuevo pedido.
 * Contiene una lista de los productos a comprar y sus cantidades.
 */
@Getter
@Setter
public class PedidoCreationRequest {
    private List<ItemRequest> items;

    /**
     * Clase interna (DTO anidado) para representar un ítem dentro de un pedido.
     */
    @Getter
    @Setter
    public static class ItemRequest {
        private Long productId;
        private int cantidad;
    }
}
