package com.empacatalog.application.controller;

import com.empacatalog.application.dto.product.ProductAuditDTO;
import com.empacatalog.application.dto.product.ProductCreationRequest;
import com.empacatalog.application.dto.product.ProductUpdateRequest;
import com.empacatalog.application.dto.product.ProductResponse;
import com.empacatalog.application.usecase.CreateProductUseCase;
import com.empacatalog.application.usecase.DeleteProductUseCase;
import com.empacatalog.application.usecase.GetProductAuditHistoryUseCase;
import com.empacatalog.application.usecase.GetProductUseCase;
import com.empacatalog.application.usecase.UpdateProductUseCase;
import com.empacatalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.DefaultRevisionEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de productos.
 * Proporciona endpoints para realizar operaciones CRUD y consultar el historial de auditoría.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetProductAuditHistoryUseCase getProductAuditHistoryUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             DeleteProductUseCase deleteProductUseCase,
                             GetProductUseCase getProductUseCase,
                             GetProductAuditHistoryUseCase getProductAuditHistoryUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.getProductAuditHistoryUseCase = getProductAuditHistoryUseCase;
    }

    // --- Endpoints de Consulta ---

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<Product> productPage = getProductUseCase.findAll(pageable);
        Page<ProductResponse> responsePage = productPage.map(this::mapToProductResponse);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = getProductUseCase.findById(id);
        return ResponseEntity.ok(mapToProductResponse(product));
    }

    /**
     * Obtiene el historial de auditoría de un producto por su ID.
     * Solo los roles 'ADMIN' y 'ADVISOR' pueden ver el historial.
     *
     * @param id El ID del producto.
     * @return Una lista de DTOs con las revisiones del producto.
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVISOR')")
    public ResponseEntity<List<ProductAuditDTO>> getProductHistory(@PathVariable Long id) {
        List<Object[]> history = getProductAuditHistoryUseCase.getProductHistory(id);
        List<ProductAuditDTO> auditDTOs = history.stream()
                .map(this::mapToProductAuditDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(auditDTOs);
    }

    // --- Endpoints de Modificación ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreationRequest request) {
        Product newProduct = createProductUseCase.createProduct(request);
        return new ResponseEntity<>(mapToProductResponse(newProduct), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest request) {
        Product updatedProduct = updateProductUseCase.updateProduct(id, request);
        return ResponseEntity.ok(mapToProductResponse(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- Métodos de mapeo ---

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setPartNumber(product.getPartNumber());
        response.setCategory(product.getCategory());
        return response;
    }

    private ProductAuditDTO mapToProductAuditDTO(Object[] auditData) {
        Product product = (Product) auditData[0];
        DefaultRevisionEntity revision = (DefaultRevisionEntity) auditData[1];
        RevisionType type = (RevisionType) auditData[2];

        ProductAuditDTO auditDTO = new ProductAuditDTO();
        auditDTO.setRevisionId((long) revision.getId());
        auditDTO.setRevisionDate(LocalDateTime.ofInstant(revision.getRevisionDate().toInstant(), ZoneId.systemDefault()));
        auditDTO.setRevisionType(type);
        auditDTO.setProduct(mapToProductResponse(product));

        return auditDTO;
    }
}
