package com.empacatalog.application.usecase;

import com.empacatalog.domain.model.ProductNotFoundException;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para eliminar un producto existente.
 */
@Component
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void deleteProduct(Long id) {
        // Verifica si el producto existe antes de intentar eliminarlo
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
