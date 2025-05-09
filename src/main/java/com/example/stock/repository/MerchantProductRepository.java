package com.example.stock.repository;

import com.example.stock.model.MerchantProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;


public interface MerchantProductRepository extends JpaRepository<MerchantProduct, Long> {
    Optional<MerchantProduct> findByMerchantIdAndProductId(Long merchantId, Long productId);
    List<MerchantProduct> findByMerchantId(Long merchantId);
    Optional<MerchantProduct> findByProductId(Long productId);


}
