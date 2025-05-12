// Chemin: src/main/java/com/example/stock/repository/PaymentMethodRepository.java
package com.example.stock.repository;

import com.example.stock.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByActive(boolean active);
}