package com.empacatalog.config;

import com.empacatalog.domain.model.InsufficientStockException;
import com.empacatalog.domain.model.PedidoNotFoundException;
import com.empacatalog.domain.model.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Captura excepciones específicas y retorna respuestas HTTP claras y consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones cuando no se encuentra un producto.
     * Retorna un estado HTTP 404 Not Found.
     *
     * @param ex La excepción ProductNotFoundException.
     * @return Una respuesta HTTP 404 con un mensaje de error.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFoundException(ProductNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja las excepciones cuando no se encuentra un pedido.
     * Retorna un estado HTTP 404 Not Found.
     *
     * @param ex La excepción PedidoNotFoundException.
     * @return Una respuesta HTTP 404 con un mensaje de error.
     */
    @ExceptionHandler(PedidoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePedidoNotFoundException(PedidoNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja las excepciones cuando hay inventario insuficiente.
     * Retorna un estado HTTP 400 Bad Request.
     *
     * @param ex La excepción InsufficientStockException.
     * @return Una respuesta HTTP 400 con un mensaje de error.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientStockException(InsufficientStockException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador genérico para cualquier otra excepción no controlada.
     * Retorna un estado HTTP 500 Internal Server Error.
     *
     * @param ex La excepción genérica.
     * @return Una respuesta HTTP 500 con un mensaje de error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
