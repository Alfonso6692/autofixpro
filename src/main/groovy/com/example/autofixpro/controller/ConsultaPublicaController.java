package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Vehiculo;
import com.example.autofixpro.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador para consultas públicas (sin autenticación)
 */
@Controller
public class ConsultaPublicaController {

    @Autowired
    private VehiculoService vehiculoService;

    /**
     * Maneja la consulta pública del estado de un vehículo por placa
     */
    @GetMapping("/consultar")
    public String consultarVehiculo(@RequestParam(required = false) String placa,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (placa == null || placa.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor ingresa una placa válida");
            return "redirect:/";
        }

        try {
            Optional<Vehiculo> vehiculoOpt = vehiculoService.findByPlaca(placa.trim().toUpperCase());

            if (vehiculoOpt.isPresent()) {
                Vehiculo vehiculo = vehiculoOpt.get();
                model.addAttribute("vehiculo", vehiculo);
                model.addAttribute("title", "Estado del Vehículo - " + placa);
                return "consulta-vehiculo";
            } else {
                redirectAttributes.addFlashAttribute("error",
                    "No se encontró ningún vehículo con la placa: " + placa);
                return "redirect:/";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al consultar el vehículo: " + e.getMessage());
            return "redirect:/";
        }
    }
}
