package com.empacatalog.application.usecase;

import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.PedidoNotFoundException;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para obtener el historial de auditoría de un pedido.
 * Utiliza AuditReader de Hibernate Envers para consultar las revisiones.
 */
@Component
public class GetPedidoAuditHistoryUseCase {

    private final EntityManager entityManager;

    public GetPedidoAuditHistoryUseCase(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPedidoHistory(Long pedidoId) {
        // Verifica si el pedido existe antes de intentar obtener su historial
        if (entityManager.find(Pedido.class, pedidoId) == null) {
            throw new PedidoNotFoundException(pedidoId);
        }

        // Crea una instancia de AuditReader a partir del EntityManager
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Construye una consulta para obtener todas las revisiones del pedido
        return auditReader.createQuery()
                .forRevisionsOfEntity(Pedido.class, false, true)
                .add(AuditEntity.id().eq(pedidoId))
                .getResultList();
    }
}
