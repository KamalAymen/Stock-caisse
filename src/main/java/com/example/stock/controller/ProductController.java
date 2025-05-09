package com.example.stock.controller;

import com.example.stock.model.Product;
import com.example.stock.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/recherche")
    public List<Product> search(
        @RequestParam("prefix") String prefix,
        @RequestParam("suffix") String suffix
    ) {
        return productService.searchByPrefixSuffix(prefix, suffix);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Product product) {
        String codeBarre = product.getCodeBarre();

        if (codeBarre != null && codeBarre.length() >= 5) {
            String prefix = codeBarre.substring(0, 5);
            String suffix = codeBarre.substring(codeBarre.length() - 2);

            List<Product> matchingProducts = productService.searchByPrefixSuffix(prefix, suffix);
            if (!matchingProducts.isEmpty()) {
                Product existingProduct = matchingProducts.get(0);
                return ResponseEntity.ok(existingProduct);
            }
        }

        Optional<Product> existing = productService.findByCodeBarre(codeBarre);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }

        productService.save(product);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Article ajouté");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
