package com.example.stock.service;

import com.example.stock.model.Merchant;
import com.example.stock.repository.MerchantRepository;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LogoService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Value("${app.logo.upload.dir:logos}")
    private String uploadDir;

    // Largeur maximale pour le logo sur le ticket de 48mm
    private static final int MAX_LOGO_WIDTH = 180;

    /**
     * Initialise le dossier de stockage des logos
     */
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage des logos", e);
        }
    }

    /**
     * Enregistre le logo d'un commerçant
     * @param merchantId ID du commerçant
     * @param logoFile fichier logo
     * @return le chemin du logo enregistré
     */
    public String saveLogo(Long merchantId, MultipartFile logoFile) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Commerçant non trouvé avec l'id: " + merchantId));

        try {
            // Vérifier si le dossier de stockage existe, sinon le créer
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                init();
            }

            // Générer un nom de fichier unique
            String originalFilename = logoFile.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            Path logoPath = Paths.get(uploadDir).resolve(newFilename);

            // Lire l'image et la redimensionner
            BufferedImage originalImage = ImageIO.read(logoFile.getInputStream());
            BufferedImage resizedImage = resizeLogo(originalImage);

            // Sauvegarder l'image redimensionnée
            File outputFile = logoPath.toFile();
            ImageIO.write(resizedImage, extension, outputFile);

            // Mettre à jour le chemin du logo dans l'entité Merchant
            merchant.setLogoPath(logoPath.toString());
            merchantRepository.save(merchant);

            return logoPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de sauvegarder le logo", e);
        }
    }

    /**
     * Redimensionne le logo pour l'adapter à la largeur du ticket
     * @param originalImage image originale
     * @return image redimensionnée
     */
    private BufferedImage resizeLogo(BufferedImage originalImage) {
        // Calculer les nouvelles dimensions tout en maintenant le ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= MAX_LOGO_WIDTH) {
            return originalImage; // Pas besoin de redimensionner
        }

        // Calculer la nouvelle hauteur en maintenant le ratio
        int newHeight = (int) ((double) originalHeight * MAX_LOGO_WIDTH / originalWidth);

        // Redimensionner l'image
        return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                MAX_LOGO_WIDTH, newHeight, Scalr.OP_ANTIALIAS);
    }

    /**
     * Supprime un logo existant
     * @param logoPath chemin du logo à supprimer
     * @return true si suppression réussie
     */
    public boolean deleteLogo(String logoPath) {
        try {
            Path path = Paths.get(logoPath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de supprimer le logo: " + logoPath, e);
        }
    }

    /**
     * Récupère le chemin du logo pour un commerçant donné
     * @param merchantId ID du commerçant
     * @return chemin du logo
     */
    public String getLogoPath(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Commerçant non trouvé avec l'id: " + merchantId));

        return merchant.getLogoPath();
    }
}