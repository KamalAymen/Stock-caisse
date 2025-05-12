// Chemin: src/main/java/com/example/stock/controller/SaleController.java
package com.example.stock.controller;

import com.example.stock.dto.SaleRequest;
import com.example.stock.dto.SaleResponse;
import com.example.stock.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody SaleRequest saleRequest) {
        try {
            SaleResponse saleResponse = saleService.createSale(saleRequest);
            return new ResponseEntity<>(saleResponse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        try {
            SaleResponse saleResponse = saleService.getSaleById(id);
            return ResponseEntity.ok(saleResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<SaleResponse>> getSalesByMerchant(@PathVariable Long merchantId) {
        return ResponseEntity.ok(saleService.getSalesByMerchant(merchantId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SaleResponse>> getSalesByDateRange(
            @RequestParam Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(saleService.getSalesByDateRange(merchantId, start, end));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSale(@PathVariable Long id) {
        try {
            SaleResponse saleResponse = saleService.cancelSale(id);
            return ResponseEntity.ok(saleResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Double>> getTotalSalesAmount(
            @RequestParam Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Double total = saleService.getTotalSalesAmount(merchantId, start, end);
        Map<String, Double> response = new HashMap<>();
        response.put("totalAmount", total);
        return ResponseEntity.ok(response);
    }
}