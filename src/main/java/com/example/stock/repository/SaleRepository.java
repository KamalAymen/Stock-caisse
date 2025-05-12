// Chemin: src/main/java/com/example/stock/repository/SaleRepository.java
package com.example.stock.repository;

import com.example.stock.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByMerchantId(Long merchantId);

    List<Sale> findByMerchantIdAndSaleDateBetween(Long merchantId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Sale s WHERE s.merchant.id = :merchantId AND s.cancelled = false ORDER BY s.saleDate DESC")
    List<Sale> findRecentSales(@Param("merchantId") Long merchantId);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.merchant.id = :merchantId AND s.saleDate BETWEEN :start AND :end AND s.cancelled = false")
    Double findTotalSalesAmount(@Param("merchantId") Long merchantId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}