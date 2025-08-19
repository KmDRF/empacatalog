package com.empacatalog.domain.model;

/**
 * Excepción personalizada para manejar el caso de inventario insuficiente.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int requested, int available) {
        super("No hay suficiente stock para el producto: " + productName +
                ". Cantidad solicitada: " + requested + ", disponible: " + available);
    }
}
