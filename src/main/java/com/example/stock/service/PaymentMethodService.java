// Chemin: src/main/java/com/example/stock/service/PaymentMethodService.java
package com.example.stock.service;

import com.example.stock.model.PaymentMethod;
import com.example.stock.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public List<PaymentMethod> getActivePaymentMethods() {
        return paymentMethodRepository.findByActive(true);
    }

    public Optional<PaymentMethod> getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    public PaymentMethod updatePaymentMethod(Long id, PaymentMethod paymentMethodDetails) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée avec l'id: " + id));

        paymentMethod.setName(paymentMethodDetails.getName());
        paymentMethod.setDescription(paymentMethodDetails.getDescription());
        paymentMethod.setActive(paymentMethodDetails.isActive());

        return paymentMethodRepository.save(paymentMethod);
    }

    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée avec l'id: " + id));

        // Plutôt que de supprimer, on désactive
        paymentMethod.setActive(false);
        paymentMethodRepository.save(paymentMethod);
    }
}