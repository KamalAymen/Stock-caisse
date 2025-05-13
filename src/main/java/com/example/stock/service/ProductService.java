package com.example.stock.service;

import com.example.stock.model.Product;
import com.example.stock.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> searchByPrefixSuffix(String prefix, String suffix) {
        return productRepository.findByCodeBarreStartingWithAndCodeBarreEndingWith(prefix, suffix);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findByCodeBarre(String codeBarre) {
        return productRepository.findByCodeBarre(codeBarre);
    }

    public Product getProductByCodeBarreWithPrefixSuffix(String codeBarre) {
        Optional<Product> optionalProduct = Optional.empty();

        if (codeBarre == null) {
            throw new RuntimeException("Le code-barre ne peut pas être null");
        }

        // Recherche exacte d'abord
        optionalProduct = productRepository.findByCodeBarre(codeBarre);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }

        // Si le code-barre est inférieur ou égal à 5 caractères, utiliser uniquement le préfixe
        if (codeBarre.length() <= 5) {
            String prefix = codeBarre;
            List<Product> matchingProducts = productRepository.findByCodeBarreStartingWith(prefix);

            if (!matchingProducts.isEmpty()) {
                return matchingProducts.get(0);
            }
        }
        // Si le code-barre est supérieur ou égal à 6 caractères, utiliser préfixe + suffixe
        else if (codeBarre.length() >= 6) {
            String prefix = codeBarre.substring(0, Math.min(6, codeBarre.length()));
            String suffix = codeBarre.length() > 2 ?
                    codeBarre.substring(codeBarre.length() - 2) :
                    "";

            List<Product> matchingProducts = productRepository.findByCodeBarreStartingWithAndCodeBarreEndingWith(prefix, suffix);

            if (!matchingProducts.isEmpty()) {
                return matchingProducts.get(0);
            }
        }

        // Si aucun produit n'a été trouvé
        throw new RuntimeException("Produit non trouvé avec ce code-barre: " + codeBarre);
    }
}