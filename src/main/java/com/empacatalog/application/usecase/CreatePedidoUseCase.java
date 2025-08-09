package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.pedido.PedidoCreationRequest;
import com.empacatalog.domain.model.*;
import com.empacatalog.domain.repository.PedidoRepository;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de uso para la creación de un nuevo pedido.
 * Contiene la lógica para procesar los ítems, calcular el total y guardar el pedido.
 */
@Component
public class CreatePedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository;

    public CreatePedidoUseCase(PedidoRepository pedidoRepository, ProductRepository productRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    /**
     * Procesa y guarda un nuevo pedido para un usuario.
     *
     * @param request Datos del pedido recibidos del cliente
     * @param user    El usuario que realiza el pedido
     * @return El pedido creado y guardado
     */
    @Transactional
    public Pedido createPedido(PedidoCreationRequest request, User user) {
        // Crea una nueva entidad de Pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUser(user);
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setEstado("PENDIENTE");

        BigDecimal totalPedido = BigDecimal.ZERO;

        // Itera sobre los ítems de la petición
        for (PedidoCreationRequest.PedidoItemRequest itemRequest : request.getItems()) {
            // Busca el producto por su ID
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.getProductId()));

            // Crea un nuevo PedidoItem
            PedidoItem nuevoItem = new PedidoItem();
            nuevoItem.setProduct(product);
            nuevoItem.setCantidad(itemRequest.getCantidad());
            nuevoItem.setPrecioUnitario(product.getPrice());

            // Añade el ítem al pedido
            nuevoPedido.addItem(nuevoItem);

            // Suma el costo del ítem al total del pedido
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getCantidad()));
            totalPedido = totalPedido.add(subtotal);
        }

        // Establece el total del pedido
        nuevoPedido.setTotal(totalPedido);

        // Guarda el pedido y sus ítems en la base de datos
        return pedidoRepository.save(nuevoPedido);
    }
}
