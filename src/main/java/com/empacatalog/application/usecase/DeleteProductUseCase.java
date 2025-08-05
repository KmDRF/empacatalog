package com.empacatalog.application.usecase;

import com.empacatalog.domain.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteProductUseCase {

    private final ProductService productService;

    public DeleteProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lógica de negocio para eliminar un producto.
     * @param id El ID del producto a eliminar.
     */
    @Transactional
    public void execute(Long id) {
        // En un caso de uso real, podrías agregar validaciones,
        // como verificar si el producto existe antes de intentar eliminarlo.
        productService.deleteProduct(id);
    }
}