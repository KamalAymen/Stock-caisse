package com.example.stock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Sale sale;

    private String receiptNumber;
    private LocalDateTime generationDate;

    @Column(columnDefinition = "TEXT")  // Modifier cette ligne pour permettre un contenu plus long
    private String receiptContent;

    private boolean emailSent = false;
    private boolean smsSent = false;
    private String customerEmail;

    // Constructeurs
    public Receipt() {
        this.generationDate = LocalDateTime.now();
    }

    public Receipt(Sale sale, String receiptNumber) {
        this.sale = sale;
        this.receiptNumber = receiptNumber;
        this.generationDate = LocalDateTime.now();
    }

    // Getters et Setters (sans changement)
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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(LocalDateTime generationDate) {
        this.generationDate = generationDate;
    }

    public String getReceiptContent() {
        return receiptContent;
    }

    public void setReceiptContent(String receiptContent) {
        this.receiptContent = receiptContent;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public boolean isSmsSent() {
        return smsSent;
    }

    public void setSmsSent(boolean smsSent) {
        this.smsSent = smsSent;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}