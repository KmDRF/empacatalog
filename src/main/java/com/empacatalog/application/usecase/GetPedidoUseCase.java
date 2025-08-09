package com.empacatalog.application.usecase;

import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.PedidoNotFoundException;
import com.empacatalog.domain.model.User;
import com.empacatalog.domain.repository.PedidoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso para consultar y obtener pedidos.
 */
@Component
public class GetPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public GetPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Busca un pedido por su ID.
     *
     * @param pedidoId El ID del pedido a buscar.
     * @return El pedido encontrado.
     * @throws PedidoNotFoundException Si el pedido no existe.
     */
    public Pedido findById(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
    }

    /**
     * Obtiene todos los pedidos de un usuario específico.
     *
     * @param user El usuario cuyos pedidos se quieren obtener.
     * @return Una lista de pedidos del usuario.
     */
    public List<Pedido> findByUser(User user) {
        return pedidoRepository.findByUserOrderByFechaCreacionDesc(user);
    }
}
