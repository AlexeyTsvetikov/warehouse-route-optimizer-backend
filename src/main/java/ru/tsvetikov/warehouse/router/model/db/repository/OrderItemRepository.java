package ru.tsvetikov.warehouse.router.model.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsvetikov.warehouse.router.model.db.entity.Order;
import ru.tsvetikov.warehouse.router.model.db.entity.OrderItem;
import ru.tsvetikov.warehouse.router.model.db.entity.Product;

import java.util.Optional;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @EntityGraph(attributePaths = {"product"})
    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    boolean existsByOrderIdAndProductId(Long orderId, Long productId);

    Optional<OrderItem> findByOrderAndProduct(Order order, Product product);

    Long id(Long id);
}