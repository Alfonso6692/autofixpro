package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador principal para manejar las rutas de inicio
 * Redirige automáticamente al dashboard cuando se accede a la raíz
 */
@Controller
public class HomeController {

    /**
     * Ruta raíz - Redirige al dashboard
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }


    /**
     * Ruta alternativa para el index
     */
    @GetMapping("/index")
    public String index() {
        return "redirect:/dashboard";
    }
}