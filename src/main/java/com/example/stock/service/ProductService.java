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

        if (codeBarre != null && codeBarre.length() >= 8) {
            String prefix = codeBarre.substring(0, 6);
            String suffix = codeBarre.substring(codeBarre.length() - 2);
            List<Product> matchingProducts = productRepository.findByCodeBarreStartingWithAndCodeBarreEndingWith(prefix, suffix);

            if (!matchingProducts.isEmpty()) {
                optionalProduct = Optional.of(matchingProducts.get(0));
            } else {
                optionalProduct = productRepository.findByCodeBarre(codeBarre);
            }
        } else {
            optionalProduct = productRepository.findByCodeBarre(codeBarre);
        }

        return optionalProduct.orElseThrow(() -> new RuntimeException("Produit non trouvé avec ce code-barre"));
    }
}
