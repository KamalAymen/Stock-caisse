package com.example.stock.dto;

public class AddMerchantProductResponse {
    private String message;
    private Long productId;
    private Long merchantProductId;

    // Constructors
    public AddMerchantProductResponse() {}

    public AddMerchantProductResponse(String message, Long productId, Long merchantProductId) {
        this.message = message;
        this.productId = productId;
        this.merchantProductId = merchantProductId;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getMerchantProductId() {
        return merchantProductId;
    }

    public void setMerchantProductId(Long merchantProductId) {
        this.merchantProductId = merchantProductId;
    }
}
