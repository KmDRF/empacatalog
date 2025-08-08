package com.empacatalog.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Representa un ítem dentro de un pedido.
 * Contiene la información del producto, la cantidad y el precio en el momento de la compra.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el producto. FetchType.EAGER carga el producto con el ítem del pedido.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer cantidad;

    // El precio se almacena en el ítem para que no cambie si el precio del producto original se actualiza.
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}
