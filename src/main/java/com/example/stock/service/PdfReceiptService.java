package com.example.stock.service;

import com.example.stock.model.Merchant;
import com.example.stock.model.Receipt;
import com.example.stock.model.Sale;
import com.example.stock.model.SaleItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReceiptService {

    @Autowired
    private ReceiptService receiptService;

    // Constantes pour la mise en page
    private static final float PAGE_WIDTH = 190; // ~48mm
    private static final float MARGIN = 10;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN);
    private static final float LINE_HEIGHT = 12;
    private static final float SMALL_LINE_HEIGHT = 10;
    private static final float LOGO_HEIGHT = 40;
    private static final float LOGO_WIDTH = 100;

    /**
     * Génère un ticket de caisse en PDF
     * @param receiptId ID du ticket
     * @return le contenu du PDF en bytes
     */
    public byte[] generatePdf(Long receiptId) {
        Receipt receipt = receiptService.getReceiptById(receiptId);
        return generatePdf(receipt);
    }

    /**
     * Génère un ticket de caisse en PDF à partir d'un objet Receipt
     * @param receipt le ticket
     * @return le contenu du PDF en bytes
     */
    public byte[] generatePdf(Receipt receipt) {
        Sale sale = receipt.getSale();
        Merchant merchant = sale.getMerchant();
        List<SaleItem> items = sale.getItems();

        try (PDDocument document = new PDDocument()) {
            // Créer une page avec la taille adaptée pour une imprimante thermique 48mm
            // La hauteur dépendra du contenu, on commence avec une estimation
            float estimatedHeight = calculateEstimatedHeight(items.size());
            PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, estimatedHeight));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = estimatedHeight - MARGIN;

                // Ajouter le logo s'il existe
                if (merchant.getLogoPath() != null && !merchant.getLogoPath().isEmpty()) {
                    File logoFile = new File(merchant.getLogoPath());
                    if (logoFile.exists()) {
                        PDImageXObject logo = PDImageXObject.createFromFile(merchant.getLogoPath(), document);
                        // Centrer le logo
                        float xPosition = (PAGE_WIDTH - LOGO_WIDTH) / 2;
                        contentStream.drawImage(logo, xPosition, yPosition - LOGO_HEIGHT, LOGO_WIDTH, LOGO_HEIGHT);
                        yPosition -= (LOGO_HEIGHT + MARGIN);
                    }
                }

                // En-tête du ticket
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(MARGIN, yPosition);

                // Nom du commerçant (centré)
                String merchantName = merchant.getName();
                float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(merchantName) / 1000 * 10;
                float textX = (PAGE_WIDTH - textWidth) / 2 - MARGIN;
                contentStream.newLineAtOffset(textX, 0);
                contentStream.showText(merchantName);
                contentStream.newLineAtOffset(-textX, -LINE_HEIGHT);

                // Adresse du commerçant (si disponible)
                if (merchant.getAddress() != null && !merchant.getAddress().isEmpty()) {
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    String address = merchant.getAddress();
                    textWidth = PDType1Font.HELVETICA.getStringWidth(address) / 1000 * 8;
                    textX = (PAGE_WIDTH - textWidth) / 2 - MARGIN;
                    contentStream.newLineAtOffset(textX, 0);
                    contentStream.showText(address);
                    contentStream.newLineAtOffset(-textX, -LINE_HEIGHT);
                }

                // Infos du ticket
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.showText("Ticket N°: " + receipt.getReceiptNumber());
                contentStream.newLineAtOffset(0, -SMALL_LINE_HEIGHT);

                String dateStr = "Date: " + DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(sale.getSaleDate());
                contentStream.showText(dateStr);
                contentStream.newLineAtOffset(0, -SMALL_LINE_HEIGHT);
                contentStream.endText();

                yPosition -= (4 * LINE_HEIGHT);

                // Ligne séparatrice
                drawLine(contentStream, MARGIN, yPosition, MARGIN + CONTENT_WIDTH, yPosition);
                yPosition -= (LINE_HEIGHT / 2);

                // En-tête des articles
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText("Article");
                contentStream.newLineAtOffset(90, 0);
                contentStream.showText("Qté");
                contentStream.newLineAtOffset(25, 0);
                contentStream.showText("Prix");
                contentStream.newLineAtOffset(30, 0);
                contentStream.showText("Total");
                contentStream.endText();

                yPosition -= LINE_HEIGHT;

                // Ligne séparatrice
                drawLine(contentStream, MARGIN, yPosition, MARGIN + CONTENT_WIDTH, yPosition);
                yPosition -= LINE_HEIGHT;

                // Liste des articles
                for (SaleItem item : items) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    contentStream.newLineAtOffset(MARGIN, yPosition);

                    // Tronquer le nom du produit si nécessaire
                    String productName = item.getProductName();
                    if (productName.length() > 15) {
                        productName = productName.substring(0, 12) + "...";
                    }
                    contentStream.showText(productName);

                    contentStream.newLineAtOffset(90, 0);
                    contentStream.showText(String.valueOf(item.getQuantity()));

                    contentStream.newLineAtOffset(25, 0);
                    contentStream.showText(String.format("%.2f", item.getPrice()));

                    contentStream.newLineAtOffset(30, 0);
                    contentStream.showText(String.format("%.2f", item.getLineTotal()));

                    contentStream.endText();

                    yPosition -= LINE_HEIGHT;
                }

                // Ligne séparatrice
                drawLine(contentStream, MARGIN, yPosition, MARGIN + CONTENT_WIDTH, yPosition);
                yPosition -= LINE_HEIGHT;

                // Totaux
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(MARGIN, yPosition);

                contentStream.showText("Sous-total HT:");
                contentStream.newLineAtOffset(115, 0);
                contentStream.showText(String.format("%.2f", sale.getTotalAmount() - sale.getTaxAmount()));
                contentStream.newLineAtOffset(-115, -LINE_HEIGHT);

                contentStream.showText("TVA (20%):");
                contentStream.newLineAtOffset(115, 0);
                contentStream.showText(String.format("%.2f", sale.getTaxAmount()));
                contentStream.newLineAtOffset(-115, -LINE_HEIGHT);

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.showText("TOTAL TTC:");
                contentStream.newLineAtOffset(115, 0);
                contentStream.showText(String.format("%.2f", sale.getTotalAmount()));
                contentStream.endText();

                yPosition -= (3 * LINE_HEIGHT);

                // Ligne séparatrice
                drawLine(contentStream, MARGIN, yPosition, MARGIN + CONTENT_WIDTH, yPosition);
                yPosition -= LINE_HEIGHT;

                // Informations de paiement
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText("Mode de paiement: " + sale.getPaymentMethod().getName());
                contentStream.endText();

                yPosition -= LINE_HEIGHT;

                // Informations client si disponibles
                if (sale.getCustomerName() != null && !sale.getCustomerName().isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    contentStream.newLineAtOffset(MARGIN, yPosition);
                    contentStream.showText("Client: " + sale.getCustomerName());
                    contentStream.endText();
                    yPosition -= LINE_HEIGHT;
                }

                // Message de remerciement
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.newLineAtOffset(MARGIN, yPosition - (2 * LINE_HEIGHT));
                String thankYouMessage = "Merci de votre visite !";
                textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(thankYouMessage) / 1000 * 8;
                textX = (PAGE_WIDTH - textWidth) / 2 - MARGIN;
                contentStream.newLineAtOffset(textX, 0);
                contentStream.showText(thankYouMessage);
                contentStream.endText();
            }

            // Écrire le document dans un tableau de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Dessine une ligne dans le document PDF
     */
    private void drawLine(PDPageContentStream contentStream, float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xEnd, yStart);
        contentStream.stroke();
    }

    /**
     * Calcule la hauteur estimée de la page en fonction du nombre d'articles
     */
    private float calculateEstimatedHeight(int itemCount) {
        // Hauteur de base pour l'en-tête, le logo, les totaux et le pied de page
        float baseHeight = 250;
        // Hauteur pour chaque article
        float itemHeight = LINE_HEIGHT;
        // Hauteur totale estimée
        return baseHeight + (itemCount * itemHeight);
    }
}