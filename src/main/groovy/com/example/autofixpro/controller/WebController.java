package com.example.autofixpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "AutoFixPro - Dashboard");
        model.addAttribute("message", "Sistema de Gestión de Taller Mecánico");
        return "dashboard";
    }

    @GetMapping("/web")
    public String home(Model model) {
        model.addAttribute("title", "AutoFixPro - Inicio");
        return "index";
    }
}