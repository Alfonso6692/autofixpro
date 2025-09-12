package com.example.autofixpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Aplicación principal de AutofixPro
 *
 * @author Tu Nombre
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
@EntityScan(basePackages = "com.example.autofixpro.entity")
public class AutofixproApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AutofixproApplication.class);

        // Configuración adicional de la aplicación
        app.setLogStartupInfo(true);

        System.out.println("===========================================");
        System.out.println("🚀 INICIANDO AUTOFIXPRO");
        System.out.println("===========================================");
        System.out.println("📱 Dashboard disponible en: http://localhost:8080");
        System.out.println("🗄️  Base de datos H2: http://localhost:8080/h2-console");
        System.out.println("===========================================");

        app.run(args);
    }
}