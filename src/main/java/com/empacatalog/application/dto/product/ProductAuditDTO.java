package com.empacatalog.application.dto.product;

import com.empacatalog.domain.model.Product;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionType;

import java.time.LocalDateTime;

/**
 * DTO para representar una versión de auditoría de un producto.
 * Incluye los datos del producto junto con la información de la revisión.
 */
@Getter
@Setter
public class ProductAuditDTO {
    private Long revisionId;
    private LocalDateTime revisionDate;
    private RevisionType revisionType; // ADD, MOD, DEL
    private ProductResponse product;
}
