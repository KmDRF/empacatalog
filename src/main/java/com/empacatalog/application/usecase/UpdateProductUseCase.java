package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.product.ProductUpdateRequest;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductNotFoundException;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para actualizar un producto existente.
 */
@Component
public class UpdateProductUseCase {

    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        // Busca el producto por ID, si no existe lanza una excepción
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Actualiza los campos con la información del DTO de petición
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setActive(request.isActive());

        // Guarda y retorna el producto actualizado
        return productRepository.save(product);
    }
}
