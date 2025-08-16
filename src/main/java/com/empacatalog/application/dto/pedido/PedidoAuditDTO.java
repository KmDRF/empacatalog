package com.empacatalog.application.dto.pedido;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionType;

import java.time.LocalDateTime;

/**
 * DTO para representar una versión de auditoría de un pedido.
 * Incluye los datos del pedido junto con la información de la revisión.
 */
@Getter
@Setter
public class PedidoAuditDTO {
    private Long revisionId;
    private LocalDateTime revisionDate;
    private RevisionType revisionType; // ADD, MOD, DEL
    private PedidoResponse pedido;
}
