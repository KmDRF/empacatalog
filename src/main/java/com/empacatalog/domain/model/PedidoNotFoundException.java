package com.empacatalog.domain.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando un pedido no es encontrado.
 * Esto resultará en una respuesta HTTP 404 NOT FOUND.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long id) {
        super(String.format("Pedido with ID '%s' not found", id));
    }
}
