package com.example.autofixpro;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String holaMundo() {
        return "🚗 ¡Hola Mundo desde AutofixPro! 🔧";
    }

    @GetMapping("/autofixpro")
    public String info() {
        return """
                🚗 AutofixPro - Sistema de Seguimiento Vehicular
                ✅ Spring Boot funcionando correctamente
                ✅ Servidor en puerto: 8080
                🔧 Endpoints: /, /autofixpro, /status
                """;
    }

    @GetMapping("/status")
    public String status() {
        return "{\"status\":\"UP\",\"service\":\"AutofixPro\"}";
    }
}