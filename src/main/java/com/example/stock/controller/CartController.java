package com.example.stock.controller;

import com.example.stock.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateCart(
            @RequestParam Long merchantId,
            @RequestBody Map<Long, Integer> cartItems) {
        try {
            cartService.validateCart(merchantId, cartItems);
            return ResponseEntity.ok("Panier validé et stock mis à jour avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
