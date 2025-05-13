package com.example.stock.model;

import jakarta.persistence.*;

@Entity
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Nouveau champ pour stocker le chemin du logo
    private String logoPath;

    // Champ pour stocker l'adresse du commerçant (utile pour le ticket)
    private String address;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Nouveaux getters et setters pour logoPath
    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    // Nouveaux getters et setters pour address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}