package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.product.ProductCreationRequest;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductAlreadyExistsException;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para crear un nuevo producto.
 * Implementa la lógica de negocio para validar la existencia del producto
 * y guardarlo en la base de datos.
 */
@Component
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Crea y guarda un nuevo producto.
     *
     * @param request DTO con los datos del nuevo producto.
     * @return La entidad Product creada.
     * @throws ProductAlreadyExistsException si un producto con el mismo número de parte ya existe.
     */
    @Transactional
    public Product createProduct(ProductCreationRequest request) {
        // Verifica si el producto con el mismo número de parte ya existe.
        if (productRepository.existsByPartNumber(request.getPartNumber())) {
            throw new ProductAlreadyExistsException(request.getPartNumber());
        }

        // Crea la entidad Product a partir del DTO.
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPartNumber(request.getPartNumber());
        product.setCategory(request.getCategory());
        product.setActive(request.isActive());

        // Guarda y retorna el producto.
        return productRepository.save(product);
    }
}
