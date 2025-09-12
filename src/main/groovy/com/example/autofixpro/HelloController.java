package com.example.autofixpro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class HelloController {

    @Autowired
    private MessageSource messageSource;

    private final Locale spanishLocale = new Locale("es", "ES");

    @GetMapping("/api")
    public String holaMundo() {
        String welcomeMessage = messageSource.getMessage("app.welcome", null, spanishLocale);
        return "🚗 " + welcomeMessage + " 🔧 Spring Boot funcionando correctamente";
    }

    @GetMapping("/autofixpro")
    public String autofixproInfo() {
        return """
                {
                    "aplicacion": "AutofixPro Backend",
                    "version": "1.0.0",
                    "status": "FUNCIONANDO",
                    "puerto": "8080",
                    "contexto": "/api",
                    "endpoints": [
                        "GET /api/",
                        "GET /api/autofixpro", 
                        "GET /api/status"
                    ]
                }
                """;
    }

    @GetMapping("/status")
    public String status() {
        return """
                {
                    "status": "UP",
                    "timestamp": """ + "\"" + java.time.Instant.now() + "\"" + """
                }
                """;
    }
}