// Chemin: src/main/java/com/example/stock/config/DataInitializer.java
package com.example.stock.config;

import com.example.stock.model.PaymentMethod;
import com.example.stock.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public void run(String... args) {
        // Vérifier si des méthodes de paiement existent déjà
        if (paymentMethodRepository.count() == 0) {
            // Créer les méthodes de paiement par défaut
            List<PaymentMethod> defaultPaymentMethods = List.of(
                    new PaymentMethod("Espèces", "Paiement en espèces"),
                    new PaymentMethod("Carte Bancaire", "Paiement par carte bancaire"),
                    new PaymentMethod("Mobile Money", "Paiement par mobile money")
            );

            paymentMethodRepository.saveAll(defaultPaymentMethods);
        }
    }
}