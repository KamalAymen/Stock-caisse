package com.example.stock.repository;

import com.example.stock.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCodeBarre(String codeBarre);
    List<Product> findByCodeBarreStartingWithAndCodeBarreEndingWith(String prefix, String suffix);
    List<Product> findByCodeBarreStartingWith(String prefix);

}
