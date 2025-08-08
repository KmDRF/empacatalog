package com.empacatalog.domain.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando un producto no es encontrado.
 * Esto resultará en una respuesta HTTP 404 NOT FOUND.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    /**
     * Constructor para la excepción.
     * Genera un mensaje de error detallado con el ID del producto que no se encontró.
     *
     * @param id El ID o número de parte del producto que no se encontró.
     */
    public ProductNotFoundException(Object id) {
        super(String.format("Product with ID/part number '%s' not found", id));
    }
}
