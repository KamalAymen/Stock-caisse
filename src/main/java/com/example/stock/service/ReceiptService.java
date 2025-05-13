// Chemin: src/main/java/com/example/stock/service/ReceiptService.java
package com.example.stock.service;

import com.example.stock.dto.ReceiptDTO;
import com.example.stock.model.Receipt;
import com.example.stock.model.Sale;
import com.example.stock.model.SaleItem;
import com.example.stock.repository.ReceiptRepository;
import com.example.stock.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private SaleRepository saleRepository;

    public Receipt generateReceipt(Sale sale) {
        // Vérifier si un reçu existe déjà pour cette vente
        Optional<Receipt> existingReceipt = receiptRepository.findBySaleId(sale.getId());
        if (existingReceipt.isPresent()) {
            return existingReceipt.get();
        }

        // Générer un numéro de reçu unique
        String receiptNumber = generateReceiptNumber(sale);

        // Créer un nouveau reçu
        Receipt receipt = new Receipt();
        receipt.setSale(sale);
        receipt.setReceiptNumber(receiptNumber);
        receipt.setGenerationDate(LocalDateTime.now());
        receipt.setCustomerEmail(sale.getCustomerName());

        // Générer le contenu du reçu
        receipt.setReceiptContent(generateReceiptContent(sale, receiptNumber));

        // Sauvegarder le reçu
        receipt = receiptRepository.save(receipt);

        // Mettre à jour la vente avec le reçu
        sale.setReceipt(receipt);
        saleRepository.save(sale);

        return receipt;
    }

    private String generateReceiptNumber(Sale sale) {
        // Format: MP-YYYYMMDD-XXXX (MP = Merchant Prefix, YYYYMMDD = date, XXXX = random UUID)
        String merchantPrefix = sale.getMerchant().getName().substring(0, Math.min(2, sale.getMerchant().getName().length())).toUpperCase();
        String datePart = DateTimeFormatter.ofPattern("yyyyMMdd").format(sale.getSaleDate());
        String uniquePart = UUID.randomUUID().toString().substring(0, 8);

        return merchantPrefix + "-" + datePart + "-" + uniquePart;
    }

    private String generateReceiptContent(Sale sale, String receiptNumber) {
        StringBuilder content = new StringBuilder();

        // En-tête
        content.append("**").append(sale.getMerchant().getName()).append("**\n");
        content.append("Ticket de caisse N° ").append(receiptNumber).append("\n");
        content.append("Date: ").append(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(sale.getSaleDate())).append("\n\n");

        // Articles
        content.append("------------------------------------------\n");
        content.append(String.format("%-20s %5s %8s %10s\n", "Article", "Qté", "P.U.", "Total"));
        content.append("------------------------------------------\n");

        for (SaleItem item : sale.getItems()) {
            content.append(String.format("%-20s %5d %8.2f %10.2f\n",
                    truncateString(item.getProductName(), 20),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getLineTotal()));
        }

        // Totaux
        content.append("------------------------------------------\n");
        content.append(String.format("%-34s %10.2f\n", "TOTAL HT:", sale.getTotalAmount() - sale.getTaxAmount()));
        content.append(String.format("%-34s %10.2f\n", "TVA 20%:", sale.getTaxAmount()));
        content.append(String.format("%-34s %10.2f\n", "TOTAL TTC:", sale.getTotalAmount()));
        content.append("------------------------------------------\n");

        // Mode de paiement
        content.append("Mode de paiement: ").append(sale.getPaymentMethod().getName()).append("\n\n");

        // Informations client
        if (sale.getCustomerName() != null && !sale.getCustomerName().isEmpty()) {
            content.append("Client: ").append(sale.getCustomerName()).append("\n");
        }

        // Notes
        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            content.append("Notes: ").append(sale.getNotes()).append("\n");
        }

        // Pied de page
        content.append("\nMerci de votre visite !");

        return content.toString();
    }

    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }

    /**
     * Récupère un ticket par son ID
     * @param receiptId ID du ticket
     * @return le ticket
     */
    public Receipt getReceiptById(Long receiptId) {
        return receiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Reçu non trouvé avec l'id: " + receiptId));
    }

    // Continuation du fichier: src/main/java/com/example/stock/service/ReceiptService.java
    public ReceiptDTO getReceiptBySaleId(Long saleId) {
        Receipt receipt = receiptRepository.findBySaleId(saleId)
                .orElseThrow(() -> new RuntimeException("Reçu non trouvé pour la vente avec l'id: " + saleId));

        return convertToDTO(receipt);
    }

    public ReceiptDTO getReceiptByNumber(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new RuntimeException("Reçu non trouvé avec le numéro: " + receiptNumber));

        return convertToDTO(receipt);
    }

    public boolean sendReceiptByEmail(Long receiptId, String email) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Reçu non trouvé avec l'id: " + receiptId));

        // Ici, vous pourriez implémenter l'envoi par e-mail
        // Pour cet exemple, nous allons juste simuler un envoi réussi

        receipt.setEmailSent(true);
        receipt.setCustomerEmail(email);
        receiptRepository.save(receipt);

        return true;
    }

    public boolean sendReceiptBySms(Long receiptId, String phoneNumber) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Reçu non trouvé avec l'id: " + receiptId));

        // Ici, vous pourriez implémenter l'envoi par SMS
        // Pour cet exemple, nous allons juste simuler un envoi réussi

        receipt.setSmsSent(true);
        receiptRepository.save(receipt);

        return true;
    }

    private ReceiptDTO convertToDTO(Receipt receipt) {
        ReceiptDTO dto = new ReceiptDTO();
        dto.setId(receipt.getId());
        dto.setSaleId(receipt.getSale().getId());
        dto.setReceiptNumber(receipt.getReceiptNumber());
        dto.setGenerationDate(receipt.getGenerationDate());
        dto.setReceiptContent(receipt.getReceiptContent());
        dto.setEmailSent(receipt.isEmailSent());
        dto.setSmsSent(receipt.isSmsSent());
        dto.setCustomerEmail(receipt.getCustomerEmail());

        return dto;
    }
}