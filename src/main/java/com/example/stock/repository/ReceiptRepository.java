// Chemin: src/main/java/com/example/stock/repository/ReceiptRepository.java
package com.example.stock.repository;

import com.example.stock.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findBySaleId(Long saleId);
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
}