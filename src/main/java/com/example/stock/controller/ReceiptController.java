// Chemin: src/main/java/com/example/stock/controller/ReceiptController.java
package com.example.stock.controller;

import com.example.stock.dto.ReceiptDTO;
import com.example.stock.service.PdfReceiptService;
import com.example.stock.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private PdfReceiptService pdfReceiptService;

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

    /**
     * Télécharger un ticket au format PDF
     * @param receiptId ID du ticket
     * @return fichier PDF
     */
    @GetMapping("/{receiptId}/pdf")
    public ResponseEntity<byte[]> getReceiptPdf(@PathVariable Long receiptId) {
        try {
            byte[] pdfContent = pdfReceiptService.generatePdf(receiptId);

            // Récupérer le numéro du ticket pour le nom du fichier
            String receiptNumber = receiptService.getReceiptById(receiptId).getReceiptNumber();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=ticket-" + receiptNumber + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (RuntimeException e) {
            // En cas d'erreur, on ne peut pas renvoyer un corps d'erreur JSON car le client attend un PDF
            // On renvoie donc une erreur 404 sans corps
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Télécharger un ticket au format PDF par ID de vente
     * @param saleId ID de la vente
     * @return fichier PDF
     */
    @GetMapping("/sale/{saleId}/pdf")
    public ResponseEntity<byte[]> getReceiptPdfBySaleId(@PathVariable Long saleId) {
        try {
            // Récupérer d'abord le ticket par ID de vente
            ReceiptDTO receiptDTO = receiptService.getReceiptBySaleId(saleId);

            // Puis générer le PDF avec l'ID du ticket
            byte[] pdfContent = pdfReceiptService.generatePdf(receiptDTO.getId());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=ticket-" + receiptDTO.getReceiptNumber() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}