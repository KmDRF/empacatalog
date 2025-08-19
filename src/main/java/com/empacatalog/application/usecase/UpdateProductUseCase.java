package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.product.ProductUpdateRequest;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductNotFoundException;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateProductUseCase {
    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setActive(request.isActive());
        existingProduct.setStock(request.getStock()); // <-- USANDO EL NUEVO CAMPO

        return productRepository.save(existingProduct);
    }
}
