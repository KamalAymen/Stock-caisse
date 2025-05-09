package com.example.stock.service;

import com.example.stock.model.MerchantProduct;
import com.example.stock.repository.MerchantProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CartService {

    @Autowired
    private MerchantProductRepository merchantProductRepository;

    public void validateCart(Long merchantId, Map<Long, Integer> cartItems) {
        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantityToDeduct = entry.getValue();

            MerchantProduct mp = merchantProductRepository.findByMerchantIdAndProductId(merchantId, productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé pour merchantId: " + merchantId + ", productId: " + productId));

            if (mp.getQuantite() < quantityToDeduct) {
                throw new RuntimeException("Stock insuffisant pour productId: " + productId);
            }

            mp.setQuantite(mp.getQuantite() - quantityToDeduct);
            merchantProductRepository.save(mp);
        }
    }
}
