package com.example.stock.service;

import com.example.stock.dto.SaleRequest;
import com.example.stock.dto.SaleResponse;
import com.example.stock.model.*;
import com.example.stock.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private MerchantProductRepository merchantProductRepository;

    @Autowired
    private ReceiptService receiptService;

    @Transactional
    public SaleResponse createSale(SaleRequest saleRequest) {
        // Récupérer le commerçant
        Merchant merchant = merchantRepository.findById(saleRequest.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Commerçant non trouvé avec l'id: " + saleRequest.getMerchantId()));

        // Récupérer la méthode de paiement
        PaymentMethod paymentMethod = paymentMethodRepository.findById(saleRequest.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée avec l'id: " + saleRequest.getPaymentMethodId()));

        // Créer une nouvelle vente
        Sale sale = new Sale();
        sale.setMerchant(merchant);
        sale.setPaymentMethod(paymentMethod);
        sale.setCustomerName(saleRequest.getCustomerName());
        sale.setCustomerPhone(saleRequest.getCustomerPhone());
        sale.setNotes(saleRequest.getNotes());
        sale.setSaleDate(LocalDateTime.now());

        // Sauvegarder la vente pour obtenir un ID
        sale = saleRepository.save(sale);

        // Ajouter les articles à la vente
        for (SaleRequest.SaleItemRequest itemRequest : saleRequest.getItems()) {
            // Récupérer le produit
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + itemRequest.getProductId()));

            // Vérifier le stock disponible
            MerchantProduct merchantProduct = merchantProductRepository
                    .findByMerchantIdAndProductId(merchant.getId(), product.getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé dans le catalogue du commerçant"));

            // S'assurer que quantity n'est pas null
            Integer quantity = itemRequest.getQuantity() != null ? itemRequest.getQuantity() : 0;

            if (merchantProduct.getQuantite() < quantity) {
                throw new RuntimeException("Stock insuffisant pour le produit: " + product.getNomProduit());
            }

            // S'assurer que price n'est pas null
            Double price = itemRequest.getPrice() != null ? itemRequest.getPrice() : merchantProduct.getPrix();

            // Créer l'article de vente
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setProductName(product.getNomProduit());
            saleItem.setCodeBarre(product.getCodeBarre());
            saleItem.setPrice(price);
            saleItem.setQuantity(quantity);
            saleItem.calculateTotal();

            // Ajouter l'article à la vente
            sale.addItem(saleItem);

            // Mettre à jour le stock
            merchantProduct.setQuantite(merchantProduct.getQuantite() - quantity);
            merchantProductRepository.save(merchantProduct);
        }

        // Recalculer le total de la vente
        sale.calculateTotal();
        sale = saleRepository.save(sale);

        // Générer le reçu
        Receipt receipt = receiptService.generateReceipt(sale);

        // Convertir et retourner la réponse
        return convertToResponse(sale);
    }

    public SaleResponse getSaleById(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'id: " + saleId));

        return convertToResponse(sale);
    }

    public List<SaleResponse> getSalesByMerchant(Long merchantId) {
        List<Sale> sales = saleRepository.findByMerchantId(merchantId);

        return sales.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<SaleResponse> getSalesByDateRange(Long merchantId, LocalDateTime start, LocalDateTime end) {
        List<Sale> sales = saleRepository.findByMerchantIdAndSaleDateBetween(merchantId, start, end);

        return sales.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SaleResponse cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'id: " + saleId));

        if (sale.isCancelled()) {
            throw new RuntimeException("Cette vente a déjà été annulée");
        }

        // Mettre à jour le statut de la vente
        sale.setCancelled(true);

        // Restaurer les stocks
        for (SaleItem item : sale.getItems()) {
            MerchantProduct merchantProduct = merchantProductRepository
                    .findByMerchantIdAndProductId(sale.getMerchant().getId(), item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé dans le catalogue du commerçant"));

            merchantProduct.setQuantite(merchantProduct.getQuantite() + item.getQuantity());
            merchantProductRepository.save(merchantProduct);
        }

        sale = saleRepository.save(sale);

        return convertToResponse(sale);
    }

    public Double getTotalSalesAmount(Long merchantId, LocalDateTime start, LocalDateTime end) {
        Double totalAmount = saleRepository.findTotalSalesAmount(merchantId, start, end);
        return totalAmount != null ? totalAmount : 0.0;
    }

    private SaleResponse convertToResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setMerchantId(sale.getMerchant().getId());
        response.setMerchantName(sale.getMerchant().getName());
        response.setSaleDate(sale.getSaleDate());
        response.setTotalAmount(sale.getTotalAmount());
        response.setTaxAmount(sale.getTaxAmount());
        response.setPaymentMethodName(sale.getPaymentMethod().getName());
        response.setCustomerName(sale.getCustomerName());
        response.setCustomerPhone(sale.getCustomerPhone());
        response.setCancelled(sale.isCancelled());

        if (sale.getReceipt() != null) {
            response.setReceiptNumber(sale.getReceipt().getReceiptNumber());
        }

        // Convertir les articles
        List<SaleResponse.SaleItemDTO> itemDTOs = new ArrayList<>();
        for (SaleItem item : sale.getItems()) {
            SaleResponse.SaleItemDTO itemDTO = new SaleResponse.SaleItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProductName());
            itemDTO.setCodeBarre(item.getCodeBarre());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setLineTotal(item.getLineTotal());

            itemDTOs.add(itemDTO);
        }

        response.setItems(itemDTOs);

        return response;
    }
}