package com.example.stock.config;

import com.example.stock.service.LogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${app.logo.upload.dir:logos}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        // Cela permettra d'accéder aux logos via une URL comme /logos/nom-du-fichier.jpg
        registry.addResourceHandler("/logos/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }

    @Bean
    CommandLineRunner init(@Autowired LogoService logoService) {
        return args -> {
            // Initialiser le dossier de logos au démarrage de l'application
            logoService.init();
        };
    }
}