package com.empacatalog.application.dto.pedido;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO para la creación de un nuevo pedido.
 * Contiene una lista de los ítems a comprar.
 */
@Getter
@Setter
public class PedidoCreationRequest {

    private List<PedidoItemRequest> items;

    @Getter
    @Setter
    public static class PedidoItemRequest {
        private Long productId;
        private Integer cantidad;
    }
}
