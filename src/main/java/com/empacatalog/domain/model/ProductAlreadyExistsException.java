package com.empacatalog.domain.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando se intenta crear un producto que ya existe
 * basado en su número de parte (partNumber).
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Product with this part number already exists")
public class ProductAlreadyExistsException extends RuntimeException {

    private final String partNumber;

    public ProductAlreadyExistsException(String partNumber) {
        super(String.format("Product with part number '%s' already exists", partNumber));
        this.partNumber = partNumber;
    }

    public ProductAlreadyExistsException(String partNumber, String message) {
        super(message);
        this.partNumber = partNumber;
    }

    public ProductAlreadyExistsException(String partNumber, String message, Throwable cause) {
        super(message, cause);
        this.partNumber = partNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }
}
