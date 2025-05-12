package com.example.stock.dto;

import java.util.List;

public class SaleRequest {
    private Long merchantId;
    private List<SaleItemRequest> items;
    private Long paymentMethodId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String notes;

    // Getters et Setters
    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public List<SaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Classe imbriquée pour les items avec initialisation par défaut
    public static class SaleItemRequest {
        private Long productId;
        private Integer quantity = 0; // Initialisation par défaut
        private Double price = 0.0; // Initialisation par défaut

        // Getters et Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity != null ? quantity : 0;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price != null ? price : 0.0;
        }
    }
}