package com.example.autofixpro.controller;

import com.example.autofixpro.entity.Usuario;
import com.example.autofixpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la autenticación y gestión de perfiles de usuario.
 * Maneja el inicio de sesión, registro, y la actualización de perfiles.
 */
@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de inicio de sesión.
     * @param error Si se produjo un error en el inicio de sesión.
     * @param logout Si el usuario acaba de cerrar sesión.
     * @param model El modelo para la vista.
     * @return La vista de inicio de sesión.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {

        // Si ya está autenticado, redirigir al dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("title", "AutoFixPro - Iniciar Sesión");

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Sesión cerrada exitosamente");
        }

        return "login";
    }

    /**
     * Muestra la página de registro de usuario.
     * @param model El modelo para la vista.
     * @return La vista de registro.
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "AutoFixPro - Registro de Usuario");
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    /**
     * Procesa el registro de un nuevo usuario.
     * @param usuario El usuario a registrar.
     * @param confirmPassword La confirmación de la contraseña.
     * @param redirectAttributes Atributos para la redirección.
     * @return Redirige a la página de inicio de sesión si el registro es exitoso, o de vuelta al registro si hay un error.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario,
                              @RequestParam String confirmPassword,
                              @RequestParam(required = false) String dni,
                              RedirectAttributes redirectAttributes) {
        try {
            // Validaciones
            if (!usuario.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/register";
            }

            if (usuario.getPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/register";
            }

            // Si no se especifica rol, asignar USER por defecto
            if (usuario.getRole() == null) {
                usuario.setRole(Usuario.Role.USER);
            }

            // Registrar usuario (el DNI se pasa al servicio)
            usuarioService.registrarUsuario(usuario, dni);

            redirectAttributes.addFlashAttribute("message", "Registro exitoso. Por favor inicia sesión.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Muestra la página de perfil del usuario.
     * @param model El modelo para la vista.
     * @return La vista del perfil.
     */
    @GetMapping("/perfil")
    public String perfilPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("title", "AutoFixPro - Mi Perfil");
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    /**
     * Cambia la contraseña del usuario.
     * @param passwordActual La contraseña actual del usuario.
     * @param nuevaPassword La nueva contraseña.
     * @param confirmarPassword La confirmación de la nueva contraseña.
     * @param redirectAttributes Atributos para la redirección.
     * @return Redirige a la página de perfil.
     */
    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                 @RequestParam String nuevaPassword,
                                 @RequestParam String confirmarPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validaciones
            if (!nuevaPassword.equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/perfil";
            }

            if (nuevaPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/perfil";
            }

            usuarioService.cambiarPassword(usuario.getId(), nuevaPassword);

            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada exitosamente");
            return "redirect:/perfil";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña");
            return "redirect:/perfil";
        }
    }
}