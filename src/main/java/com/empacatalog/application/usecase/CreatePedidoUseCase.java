package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.pedido.PedidoCreationRequest;
import com.empacatalog.domain.model.InsufficientStockException;
import com.empacatalog.domain.model.Pedido;
import com.empacatalog.domain.model.PedidoItem;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductNotFoundException;
import com.empacatalog.domain.model.User;
import com.empacatalog.domain.repository.PedidoRepository;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Caso de uso para crear un nuevo pedido.
 * Valida el stock antes de crear el pedido y actualiza el inventario.
 */
@Component
public class CreatePedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository; // Inyecta el repositorio de productos

    public CreatePedidoUseCase(PedidoRepository pedidoRepository, ProductRepository productRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Pedido createPedido(PedidoCreationRequest request, User user) {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUser(user);
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setEstado("PENDIENTE");

        // Calcular el total y validar el stock
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoCreationRequest.ItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.getProductId()));

            // --- LÓGICA DE VALIDACIÓN DE INVENTARIO ---
            if (product.getStock() < itemRequest.getCantidad()) {
                throw new InsufficientStockException(product.getName(), itemRequest.getCantidad(), product.getStock());
            }

            // Actualizar el stock del producto
            product.setStock(product.getStock() - itemRequest.getCantidad());
            productRepository.save(product);

            // Crear el ítem del pedido
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setProduct(product);
            pedidoItem.setCantidad(itemRequest.getCantidad());
            pedidoItem.setPrecioUnitario(product.getPrice());
            pedidoItem.setPedido(nuevoPedido);
            nuevoPedido.getItems().add(pedidoItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getCantidad())));
        }

        nuevoPedido.setTotal(total);
        return pedidoRepository.save(nuevoPedido);
    }
}
