package com.empacatalog.application.usecase;

import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductNotFoundException;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para obtener el historial de auditoría de un producto.
 * Utiliza AuditReader de Hibernate Envers para consultar las revisiones.
 */
@Component
public class GetProductAuditHistoryUseCase {

    private final EntityManager entityManager;

    public GetProductAuditHistoryUseCase(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProductHistory(Long productId) {
        // Verifica si el producto existe antes de intentar obtener su historial
        if (entityManager.find(Product.class, productId) == null) {
            throw new ProductNotFoundException(productId);
        }

        // Crea una instancia de AuditReader a partir del EntityManager
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Construye una consulta para obtener todas las revisiones del producto
        return auditReader.createQuery()
                .forRevisionsOfEntity(Product.class, false, true)
                .add(AuditEntity.id().eq(productId))
                .getResultList();
    }
}
