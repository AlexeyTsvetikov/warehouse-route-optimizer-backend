package ru.tsvetikov.warehouse.router.model.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.tsvetikov.warehouse.router.model.db.entity.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    Optional<Product> findBySkuIgnoreCase(String sku);
    
    boolean existsBySkuIgnoreCase(String sku);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchActive(@Param("query") String query, Pageable pageable);
}
