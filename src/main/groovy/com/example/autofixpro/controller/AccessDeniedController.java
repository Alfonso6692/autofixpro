package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para manejar la página de acceso denegado.
 */
@Controller
public class AccessDeniedController {

    /**
     * Muestra la página de acceso denegado cuando un usuario intenta
     * acceder a un recurso para el que no tiene permisos.
     *
     * @return La vista de acceso denegado.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
