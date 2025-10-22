package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar las vistas web relacionadas con SMS.
 */
@Controller
@RequestMapping("/sms")
public class SmsWebController {

    /**
     * Muestra la página principal de SMS.
     * @return El nombre de la vista 'sms-cliente'.
     */
    @GetMapping
    public String smsPage() {
        return "sms-cliente";
    }

    /**
     * Muestra la página de SMS para el cliente.
     * Es una ruta alternativa para la misma página.
     * @return El nombre de la vista 'sms-cliente'.
     */
    @GetMapping("/cliente")
    public String smsClientePage() {
        return "sms-cliente";
    }
}