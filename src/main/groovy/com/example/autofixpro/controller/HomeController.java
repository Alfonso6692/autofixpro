package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para las páginas de inicio de la aplicación.
 */
@Controller
public class HomeController {

    /**
     * Muestra la página de inicio principal.
     * @return El nombre de la vista 'index'.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Redirige desde /index al dashboard de la aplicación.
     * @return Una cadena de redirección a '/dashboard'.
     */
    @GetMapping("/index")
    public String index() {
        return "redirect:/dashboard";
    }
}