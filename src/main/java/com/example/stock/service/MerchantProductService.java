package com.example.stock.service;

import com.example.stock.dto.MerchantProductResponse;
import com.example.stock.model.Category;
import com.example.stock.model.Merchant;
import com.example.stock.model.MerchantProduct;
import com.example.stock.model.Product;
import com.example.stock.repository.CategoryRepository;
import com.example.stock.repository.MerchantProductRepository;
import com.example.stock.repository.MerchantRepository;
import com.example.stock.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class MerchantProductService {

    @Autowired
    private MerchantProductRepository merchantProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Map<String, Object> addOrUpdate(Map<String, Object> payload) {
        String codeBarre = (String) payload.get("codeBarre");
        String nomProduit = (String) payload.get("nomProduit");
        Integer categoryId = (Integer) payload.get("categoryId");
        Double prix = Double.valueOf(payload.get("prix").toString());
        Integer quantite = (Integer) payload.get("quantite");
        Long merchantId = Long.valueOf(payload.get("merchantId").toString());

        Optional<Product> optionalProduct = Optional.empty();

        // Gestion prefix/suffix
        if (codeBarre != null) {
            if (codeBarre.length() >= 8) {
                String prefix = codeBarre.substring(0, 6);
                String suffix = codeBarre.substring(codeBarre.length() - 2);
                List<Product> matchingProducts = productRepository.findByCodeBarreStartingWithAndCodeBarreEndingWith(prefix, suffix);
        
                if (!matchingProducts.isEmpty()) {
                    optionalProduct = Optional.of(matchingProducts.get(0));
                } else {
                    optionalProduct = productRepository.findByCodeBarre(codeBarre);
                }
            } else if (codeBarre.length() <= 6) {
                String prefix = codeBarre;
                List<Product> matchingProducts = productRepository.findByCodeBarreStartingWith(prefix);
        
                if (!matchingProducts.isEmpty()) {
                    optionalProduct = Optional.of(matchingProducts.get(0));
                } else {
                    optionalProduct = productRepository.findByCodeBarre(codeBarre);
                }
            } else {
                optionalProduct = productRepository.findByCodeBarre(codeBarre);
            }
        }
        

        Product product;
        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            product = new Product();
            product.setCodeBarre(codeBarre);
            product.setNomProduit(nomProduit);
            Category category = categoryRepository.findById(Long.valueOf(categoryId))
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);

            product = productRepository.save(product);
        }

        Optional<MerchantProduct> optionalMerchantProduct =
                merchantProductRepository.findByMerchantIdAndProductId(merchantId, product.getId());

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        MerchantProduct merchantProduct;
        String message;
        if (optionalMerchantProduct.isPresent()) {
            merchantProduct = optionalMerchantProduct.get();
            merchantProduct.setPrix(prix);
            merchantProduct.setQuantite(quantite);
            message = "Produit existant, prix et quantité mis à jour pour le commerçant.";
        } else {
            merchantProduct = new MerchantProduct();
            merchantProduct.setMerchant(merchant);
            merchantProduct.setProduct(product);
            merchantProduct.setPrix(prix);
            merchantProduct.setQuantite(quantite);
            message = "Produit ajouté au catalogue du commerçant.";
        }
        merchantProduct = merchantProductRepository.save(merchantProduct);

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("productId", product.getId());
        response.put("merchantProductId", merchantProduct.getId());
        return response;
    }
    public void delete(Long id) {
        if (!merchantProductRepository.existsById(id)) {
            throw new RuntimeException("MerchantProduct non trouvé pour id: " + id);
        }
        merchantProductRepository.deleteById(id);
    }

public MerchantProductResponse getByMerchantIdAndProductId(Long merchantId, Long productId) {
    MerchantProduct merchantProduct = merchantProductRepository
        .findByMerchantIdAndProductId(merchantId, productId)
        .orElseThrow(() -> new RuntimeException("Produit non trouvé pour ce commerçant"));

        return new MerchantProductResponse(
            merchantProduct.getId(),
            merchantProduct.getProduct().getId(),
            merchantProduct.getProduct().getNomProduit(),
            merchantProduct.getProduct().getCodeBarre(),
            merchantProduct.getProduct().getCategory() != null ? merchantProduct.getProduct().getCategory().getNom() : null,
            merchantProduct.getPrix(),
            merchantProduct.getQuantite()
        );
}

public void deleteByProductId(Long merchantId, Long productId) {
    MerchantProduct mp = merchantProductRepository.findByMerchantIdAndProductId(merchantId, productId)
        .orElseThrow(() -> new RuntimeException("Produit non trouvé pour ce commerçant"));

    merchantProductRepository.delete(mp);
}


public List<MerchantProductResponse> getAllByMerchantId(Long merchantId) {
    List<MerchantProduct> merchantProducts = merchantProductRepository.findByMerchantId(merchantId);
    List<MerchantProductResponse> responses = new ArrayList<>();
    for (MerchantProduct mp : merchantProducts) {
        responses.add(new MerchantProductResponse(
            mp.getId(),
            mp.getProduct().getId(),
            mp.getProduct().getNomProduit(),
            mp.getProduct().getCodeBarre(),
            mp.getProduct().getCategory() != null ? mp.getProduct().getCategory().getNom() : null,
            mp.getPrix(),
            mp.getQuantite()
        ));
    }
    return responses;
  
    
    
}


    
}
