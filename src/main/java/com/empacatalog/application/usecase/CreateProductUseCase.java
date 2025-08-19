package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.product.ProductCreationRequest;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProductUseCase {
    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(ProductCreationRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPartNumber(request.getPartNumber());
        product.setCategory(request.getCategory());
        product.setActive(request.isActive());
        product.setStock(request.getStock()); // <-- USANDO EL NUEVO CAMPO
        return productRepository.save(product);
    }
}
