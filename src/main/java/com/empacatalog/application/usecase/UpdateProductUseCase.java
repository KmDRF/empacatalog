package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.ProductDTO;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UpdateProductUseCase {

    private final ProductService productService;

    public UpdateProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lógica de negocio para actualizar un producto existente.
     * @param id El ID del producto a actualizar.
     * @param productDTO El DTO con los datos actualizados.
     * @return El DTO del producto actualizado, o un Optional vacío si no se encuentra.
     */
    @Transactional
    public Optional<ProductDTO> execute(Long id, ProductDTO productDTO) {
        // 1. Busca el producto existente por su ID.
        return productService.findProductById(id)
                .map(existingProduct -> {
                    // 2. Si se encuentra, actualiza sus campos con los datos del DTO.
                    existingProduct.setPartNumber(productDTO.getPartNumber());
                    existingProduct.setName(productDTO.getName());
                    existingProduct.setDescription(productDTO.getDescription());
                    existingProduct.setSpecifications(productDTO.getSpecifications());
                    existingProduct.setCategory(productDTO.getCategory());
                    existingProduct.setPrice(productDTO.getPrice());
                    existingProduct.setStockQuantity(productDTO.getStockQuantity());
                    existingProduct.setImageUrl(productDTO.getImageUrl());

                    // 3. Guarda la entidad actualizada en la base de datos.
                    Product updatedProduct = productService.saveProduct(existingProduct);

                    // 4. Convierte la entidad actualizada a un DTO para el retorno.
                    return ProductDTO.fromEntity(updatedProduct);
                });
    }
}