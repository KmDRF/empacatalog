package com.empacatalog.application.controller;

import com.empacatalog.application.dto.ProductDTO;
import com.empacatalog.application.usecase.CreateProductUseCase;
import com.empacatalog.application.usecase.DeleteProductUseCase;
import com.empacatalog.application.usecase.GetProductUseCase;
import com.empacatalog.application.usecase.UpdateProductUseCase;
import com.empacatalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Inyecta todos los casos de uso para manejar la lógica de negocio
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetProductUseCase getProductUseCase; // Nuevo caso de uso para consultas

    public ProductController(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            GetProductUseCase getProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.getProductUseCase = getProductUseCase;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        // Delega la obtención de productos al caso de uso de consulta
        Page<Product> products = getProductUseCase.findAll(pageable);
        Page<ProductDTO> productDTOs = products.map(ProductDTO::fromEntity);
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        // Delega la búsqueda por ID al caso de uso de consulta
        Product product = getProductUseCase.findById(id);
        ProductDTO productDTO = ProductDTO.fromEntity(product);
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProductDTO = createProductUseCase.execute(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return updateProductUseCase.execute(id, productDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
