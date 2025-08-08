package com.empacatalog.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa un pedido realizado por un cliente.
 * Contiene la información del cliente, el estado del pedido y una colección de sus ítems.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario (cliente) que realiza el pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Un pedido tiene muchos ítems. CascadeType.ALL significa que las operaciones
    // (persist, remove) se propagarán a los ítems.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PedidoItem> items = new HashSet<>();

    public void addItem(PedidoItem item) {
        items.add(item);
        item.setPedido(this);
    }

    public void removeItem(PedidoItem item) {
        items.remove(item);
        item.setPedido(null);
    }
}
