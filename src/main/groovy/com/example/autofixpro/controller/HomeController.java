package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para las páginas de inicio de la aplicación.
 */
@Controller
public class HomeController {

    /**
     * Muestra la página de inicio principal (index.html).
     * @return El nombre de la vista 'index'.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Muestra la página de inicio (index.html).
     * @return El nombre de la vista 'index'.
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }
}