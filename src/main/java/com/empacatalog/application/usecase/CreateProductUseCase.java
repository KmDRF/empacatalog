package com.empacatalog.application.usecase;

import com.empacatalog.application.dto.ProductDTO;
import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductAlreadyExistsException; // Importa la excepción personalizada
import com.empacatalog.domain.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class CreateProductUseCase {

    private final ProductService productService;

    public CreateProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lógica de negocio para crear un nuevo producto.
     * Este método encapsula la validación y la persistencia.
     * @param productDTO El DTO con los datos del nuevo producto.
     * @return El DTO del producto creado.
     */
    @Transactional
    public ProductDTO execute(ProductDTO productDTO) {
        // 1. Validar si ya existe un producto con el mismo partNumber.
        Optional<Product> existingProduct = productService.findProductByPartNumber(productDTO.getPartNumber());
        if (existingProduct.isPresent()) {
            throw new ProductAlreadyExistsException("El producto con partNumber '" + productDTO.getPartNumber() + "' ya existe.");
        }

        // 2. Convertir el DTO a la entidad Product.
        Product product = productDTO.toEntity();

        // 3. Persistir la entidad en la base de datos a través del servicio.
        Product createdProduct = productService.saveProduct(product);

        // 4. Convertir la entidad persistida de nuevo a un DTO para la respuesta.
        return ProductDTO.fromEntity(createdProduct);
    }
}
