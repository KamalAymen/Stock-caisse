// Chemin: src/main/java/com/example/stock/controller/CashRegisterController.java
package com.example.stock.controller;

import com.example.stock.dto.MerchantProductResponse;
import com.example.stock.model.Product;
import com.example.stock.service.MerchantProductService;
import com.example.stock.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cash-register")
public class CashRegisterController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantProductService merchantProductService;

    @GetMapping("/scan")
    public ResponseEntity<?> scanProduct(
            @RequestParam String codeBarre,
            @RequestParam Long merchantId) {
        try {
            // Trouver le produit par code-barre avec la logique de préfixe/suffixe
            Product product = productService.getProductByCodeBarreWithPrefixSuffix(codeBarre);

            // Récupérer les informations du produit spécifiques au commerçant
            MerchantProductResponse merchantProduct = merchantProductService.getByMerchantIdAndProductId(
                    merchantId, product.getId());

            return ResponseEntity.ok(merchantProduct);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/product-search")
    public ResponseEntity<?> searchProduct(
            @RequestParam String term,
            @RequestParam Long merchantId) {
        // Ici, on pourrait implémenter une recherche textuelle des produits
        // Pour cet exemple, on utilise simplement la recherche par préfixe/suffixe

        try {
            if (term.length() < 3) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le terme de recherche doit contenir au moins 3 caractères");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Comme nous n'avons pas encore de méthode de recherche textuelle,
            // utilisez la recherche par préfixe comme approximation
            return ResponseEntity.ok(productService.searchByPrefixSuffix(term, ""));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}