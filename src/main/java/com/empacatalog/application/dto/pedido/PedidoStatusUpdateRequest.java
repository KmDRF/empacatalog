package com.empacatalog.application.dto.pedido;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para actualizar el estado de un pedido.
 * Solo contiene el nuevo estado.
 */
@Getter
@Setter
public class PedidoStatusUpdateRequest {
    private String nuevoEstado;
}
