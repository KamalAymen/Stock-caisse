// Chemin: src/main/java/com/example/stock/repository/SaleItemRepository.java
package com.example.stock.repository;

import com.example.stock.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);

    @Query("SELECT si FROM SaleItem si WHERE si.sale.merchant.id = :merchantId AND si.sale.saleDate BETWEEN :start AND :end AND si.product.id = :productId AND si.sale.cancelled = false")
    List<SaleItem> findByProductAndPeriod(@Param("merchantId") Long merchantId, @Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.product.id = :productId AND si.sale.merchant.id = :merchantId AND si.sale.cancelled = false")
    Integer getTotalSoldByProduct(@Param("productId") Long productId, @Param("merchantId") Long merchantId);
}