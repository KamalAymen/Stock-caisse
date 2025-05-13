package com.example.stock.controller;

import com.example.stock.service.LogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/merchants")
public class MerchantLogoController {

    @Autowired
    private LogoService logoService;

    /**
     * Upload d'un logo pour un commerçant
     * @param merchantId ID du commerçant
     * @param file fichier logo
     * @return réponse avec le chemin du logo
     */
    @PostMapping("/{merchantId}/logo")
    public ResponseEntity<?> uploadLogo(
            @PathVariable Long merchantId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Veuillez sélectionner un fichier");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Vérifier le type de fichier
            String contentType = file.getContentType();
            if (contentType == null ||
                    !(contentType.equals("image/jpeg") ||
                            contentType.equals("image/png") ||
                            contentType.equals("image/gif"))) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le fichier doit être une image (JPEG, PNG ou GIF)");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Sauvegarder le logo
            String logoPath = logoService.saveLogo(merchantId, file);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logo téléchargé avec succès");
            response.put("logoPath", logoPath);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime le logo d'un commerçant
     * @param merchantId ID du commerçant
     * @return réponse de statut
     */
    @DeleteMapping("/{merchantId}/logo")
    public ResponseEntity<?> deleteLogo(@PathVariable Long merchantId) {
        try {
            String logoPath = logoService.getLogoPath(merchantId);
            if (logoPath == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Aucun logo trouvé pour ce commerçant");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            boolean deleted = logoService.deleteLogo(logoPath);

            Map<String, String> response = new HashMap<>();
            if (deleted) {
                response.put("message", "Logo supprimé avec succès");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "Impossible de supprimer le logo");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}