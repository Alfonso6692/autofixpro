package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Cliente;
import com.example.autofixpro.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "registered", required = false) String registered) {
        model.addAttribute("title", "AutoFixPro - Iniciar Sesión");
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (registered != null) {
            model.addAttribute("success", "Cliente registrado exitosamente");
        }
        return "login";
    }

    // Spring Security maneja el POST login automáticamente

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "AutoFixPro - Registrar Cliente");
        model.addAttribute("cliente", new Cliente());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("cliente") Cliente cliente,
                                 RedirectAttributes redirectAttributes) {
        try {
            clienteService.save(cliente);
            redirectAttributes.addAttribute("registered", "true");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar cliente: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}