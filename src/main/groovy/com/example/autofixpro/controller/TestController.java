package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de prueba para verificar la configuración del enrutamiento.
 */
@Controller
public class TestController {

    /**
     * Endpoint de prueba que devuelve la página de inicio.
     * @return El nombre de la vista 'index'.
     */
    @GetMapping("/test")
    public String test() {
        return "index";
    }
}