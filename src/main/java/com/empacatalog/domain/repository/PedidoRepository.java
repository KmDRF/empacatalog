package com.empacatalog.domain.repository;

import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de Spring Data JPA para la entidad Pedido.
 * Proporciona operaciones de base de datos para los pedidos.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Encuentra todos los pedidos de un usuario específico, ordenados por fecha de forma descendente
    List<Pedido> findByUserOrderByFechaCreacionDesc(User user);

    // Encuentra todos los pedidos con un estado específico
    List<Pedido> findByEstado(String estado);
}
