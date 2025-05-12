// Chemin: src/main/java/com/example/stock/service/SaleItemService.java
package com.example.stock.service;

import com.example.stock.model.SaleItem;
import com.example.stock.repository.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleItemService {

    @Autowired
    private SaleItemRepository saleItemRepository;

    public List<SaleItem> getSaleItemsBySaleId(Long saleId) {
        return saleItemRepository.findBySaleId(saleId);
    }

    public List<SaleItem> getSaleItemsByProductAndPeriod(Long merchantId, Long productId, LocalDateTime start, LocalDateTime end) {
        return saleItemRepository.findByProductAndPeriod(merchantId, productId, start, end);
    }

    public Integer getTotalSoldByProduct(Long productId, Long merchantId) {
        Integer total = saleItemRepository.getTotalSoldByProduct(productId, merchantId);
        return total != null ? total : 0;
    }
}