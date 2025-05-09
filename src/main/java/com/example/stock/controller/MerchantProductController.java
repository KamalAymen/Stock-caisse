package com.example.stock.controller;

import com.example.stock.dto.MerchantProductResponse;
import com.example.stock.service.MerchantProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant-products")
public class MerchantProductController {

    @Autowired
    private MerchantProductService merchantProductService;

    @PostMapping("/add/{merchantId}")
    public Map<String, Object> addOrUpdateMerchantProduct(
            @PathVariable Long merchantId,
            @RequestBody Map<String, Object> payload) {
        // Ajouter merchantId au payload pour le transmettre au service
        payload.put("merchantId", merchantId.toString());
        return merchantProductService.addOrUpdate(payload);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMerchantProducts(@RequestParam Long merchantId) {
        return ResponseEntity.ok(merchantProductService.getAllByMerchantId(merchantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMerchantProduct(@PathVariable("id") Long id) {
        merchantProductService.delete(id);
        return ResponseEntity.ok("Produit supprimé du commerçant");
    }

    @GetMapping
    public ResponseEntity<?> getMerchantProductByProductId(
            @RequestParam Long merchantId,
            @RequestParam Long productId) {
        try {
            MerchantProductResponse response = merchantProductService.getByMerchantIdAndProductId(merchantId, productId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMerchantProductByProductId(
            @RequestParam Long merchantId,
            @RequestParam Long productId) {
        merchantProductService.deleteByProductId(merchantId, productId);
        return ResponseEntity.ok("Produit supprimé avec succès");
    }
}