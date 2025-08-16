package com.empacatalog.application.usecase;

import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.PedidoNotFoundException;
import com.empacatalog.domain.repository.PedidoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para actualizar el estado de un pedido.
 */
@Component
public class UpdatePedidoStatusUseCase {

    private final PedidoRepository pedidoRepository;

    public UpdatePedidoStatusUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Actualiza el estado de un pedido existente.
     *
     * @param pedidoId El ID del pedido a actualizar.
     * @param nuevoEstado El nuevo estado para el pedido.
     * @return El pedido actualizado.
     * @throws PedidoNotFoundException Si el pedido no es encontrado.
     */
    @Transactional
    public Pedido updateStatus(Long pedidoId, String nuevoEstado) {
        // Busca el pedido por ID o lanza una excepción si no lo encuentra.
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        // Establece el nuevo estado.
        pedido.setEstado(nuevoEstado);

        // Guarda y retorna el pedido actualizado.
        return pedidoRepository.save(pedido);
    }
}
