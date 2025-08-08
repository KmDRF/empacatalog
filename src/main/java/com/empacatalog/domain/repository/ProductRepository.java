package com.empacatalog.domain.repository;

import com.empacatalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Product.
 * Proporciona métodos para interactuar con la base de datos,
 * incluyendo consultas personalizadas.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Método para buscar un producto por su número de parte
    Optional<Product> findByPartNumber(String partNumber);

    // Método para buscar productos por su categoría
    List<Product> findByCategory(String category);

    // Método para buscar productos por su estado de activación
    List<Product> findByActive(boolean active);

    // Método para buscar productos por un fragmento de nombre (ignorando mayúsculas y minúsculas)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Consulta personalizada para buscar productos en un rango de precios
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Método para verificar si un producto con un número de parte específico ya existe
    boolean existsByPartNumber(String partNumber);
}
