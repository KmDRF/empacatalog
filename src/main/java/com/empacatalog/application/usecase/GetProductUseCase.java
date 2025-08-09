package com.empacatalog.application.usecase;

import com.empacatalog.domain.model.Product;
import com.empacatalog.domain.model.ProductNotFoundException;
import com.empacatalog.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso para consultar productos.
 * Contiene la lógica de negocio para obtener y buscar productos.
 */
@Component
public class GetProductUseCase {

    private final ProductRepository productRepository;

    @Autowired
    public GetProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Obtiene un producto por su ID.
     *
     * @param productId El ID del producto a buscar
     * @return El producto encontrado
     * @throws ProductNotFoundException Si no se encuentra el producto
     */
    public Product findById(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    /**
     * Obtiene un producto por su número de parte.
     *
     * @param partNumber El número de parte del producto a buscar
     * @return El producto encontrado
     * @throws ProductNotFoundException Si no se encuentra el producto
     */
    public Product findByPartNumber(String partNumber) {
        if (partNumber == null || partNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Part number cannot be null or empty");
        }

        return productRepository.findByPartNumber(partNumber)
                .orElseThrow(() -> new ProductNotFoundException(partNumber));
    }

    /**
     * Obtiene todos los productos con paginación.
     *
     * @param pageable Información de paginación
     * @return Página de productos
     */
    public Page<Product> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        return productRepository.findAll(pageable);
    }

    /**
     * Obtiene todos los productos (sin paginación).
     * Usar con precaución en bases de datos grandes.
     *
     * @return Lista de todos los productos
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Busca productos por categoría.
     *
     * @param category La categoría a buscar
     * @return Lista de productos de la categoría especificada
     */
    public List<Product> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        return productRepository.findByCategory(category);
    }

    /**
     * Busca productos activos o inactivos.
     *
     * @param active El estado activo a buscar
     * @return Lista de productos con el estado especificado
     */
    public List<Product> findByActiveStatus(boolean active) {
        return productRepository.findByActive(active);
    }

    /**
     * Busca productos por nombre (búsqueda parcial, case insensitive).
     *
     * @param name El texto a buscar en el nombre
     * @return Lista de productos que coincidan
     */
    public List<Product> findByNameContaining(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        return productRepository.findByNameContainingIgnoreCase(name.trim());
    }

    /**
     * Busca productos en un rango de precios.
     *
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de productos en el rango de precios
     */
    public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Price range values cannot be null");
        }

        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Price values cannot be negative");
        }

        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Verifica si existe un producto con el número de parte especificado.
     *
     * @param partNumber El número de parte a verificar
     * @return true si existe, false si no
     */
    public boolean existsByPartNumber(String partNumber) {
        if (partNumber == null || partNumber.trim().isEmpty()) {
            return false;
        }

        return productRepository.existsByPartNumber(partNumber);
    }
}

