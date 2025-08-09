package com.empacatalog.application.dto.pedido;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la respuesta de un pedido completo.
 * Muestra los detalles del pedido, del usuario y una lista de sus ítems.
 */
@Getter
@Setter
public class PedidoResponse {
    private Long id;
    private Long userId; // Se expone solo el ID del usuario
    private String userEmail;
    private LocalDateTime fechaCreacion;
    private String estado;
    private BigDecimal total;
    private List<PedidoItemResponse> items;
}
