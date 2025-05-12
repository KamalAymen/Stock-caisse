// Chemin: src/main/java/com/example/stock/controller/ReceiptController.java
package com.example.stock.controller;

import com.example.stock.dto.ReceiptDTO;
import com.example.stock.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<?> getReceiptBySaleId(@PathVariable Long saleId) {
        try {
            ReceiptDTO receiptDTO = receiptService.getReceiptBySaleId(saleId);
            return ResponseEntity.ok(receiptDTO);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/number/{receiptNumber}")
    public ResponseEntity<?> getReceiptByNumber(@PathVariable String receiptNumber) {
        try {
            ReceiptDTO receiptDTO = receiptService.getReceiptByNumber(receiptNumber);
            return ResponseEntity.ok(receiptDTO);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PostMapping("/{receiptId}/send-email")
    public ResponseEntity<?> sendReceiptByEmail(
            @PathVariable Long receiptId,
            @RequestParam String email) {
        try {
            boolean sent = receiptService.sendReceiptByEmail(receiptId, email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reçu envoyé par e-mail avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PostMapping("/{receiptId}/send-sms")
    public ResponseEntity<?> sendReceiptBySms(
            @PathVariable Long receiptId,
            @RequestParam String phoneNumber) {
        try {
            boolean sent = receiptService.sendReceiptBySms(receiptId, phoneNumber);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reçu envoyé par SMS avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}