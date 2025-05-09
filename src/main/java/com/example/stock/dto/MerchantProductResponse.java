package com.example.stock.dto;

public class MerchantProductResponse {
    private Long merchantProductId;
    private Long productId;
    private String nomProduit;
    private String codeBarre;
    private String categoryName;
    private Double prix;
    private Integer quantite;

    public MerchantProductResponse(Long merchantProductId, Long productId, String nomProduit, String codeBarre,
                                   String categoryName, Double prix, Integer quantite) {
        this.merchantProductId = merchantProductId;
        this.productId = productId;
        this.nomProduit = nomProduit;
        this.codeBarre = codeBarre;
        this.categoryName = categoryName;
        this.prix = prix;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getMerchantProductId() { return merchantProductId; }
    public void setMerchantProductId(Long merchantProductId) { this.merchantProductId = merchantProductId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getNomProduit() { return nomProduit; }
    public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }

    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}
