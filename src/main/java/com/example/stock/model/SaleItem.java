package com.example.stock.model;

import jakarta.persistence.*;

@Entity
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Sale sale;

    @ManyToOne
    private Product product;

    private String productName;
    private String codeBarre;
    private Double price;
    private Integer quantity;
    private Double lineTotal;

    // Constructeurs
    public SaleItem() {
        this.quantity = 0;  // Initialiser avec une valeur par défaut
        this.price = 0.0;   // Initialiser avec une valeur par défaut
        this.lineTotal = 0.0; // Initialiser avec une valeur par défaut
    }

    public SaleItem(Product product, Double price, Integer quantity) {
        this.product = product;
        this.productName = product.getNomProduit();
        this.codeBarre = product.getCodeBarre();
        this.price = price != null ? price : 0.0;
        this.quantity = quantity != null ? quantity : 0;
        this.calculateTotal();
    }

    // Méthodes utilitaires
    public void calculateTotal() {
        if (this.price != null && this.quantity != null) {
            this.lineTotal = this.price * this.quantity;
        } else {
            this.lineTotal = 0.0;
        }
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productName = product.getNomProduit();
            this.codeBarre = product.getCodeBarre();
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCodeBarre() {
        return codeBarre;
    }

    public void setCodeBarre(String codeBarre) {
        this.codeBarre = codeBarre;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
        calculateTotal();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}