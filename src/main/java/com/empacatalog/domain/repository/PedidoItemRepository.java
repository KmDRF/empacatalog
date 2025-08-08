package com.empacatalog.domain.repository;

import com.empacatalog.domain.model.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Spring Data JPA para la entidad PedidoItem.
 * Proporciona operaciones de base de datos para los ítems de un pedido.
 */
@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    // Los métodos básicos de CRUD ya están disponibles por herencia
}
