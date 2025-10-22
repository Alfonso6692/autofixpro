package com.example.autofixpro.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador personalizado de autenticación exitosa.
 * Redirige a diferentes páginas según el rol del usuario.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {

        // Obtener el rol del usuario autenticado
        String redirectUrl = "/dashboard"; // URL por defecto

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/dashboard"; // Administradores al dashboard principal
                break;
            } else if (role.equals("ROLE_TECNICO")) {
                redirectUrl = "/dashboard"; // Técnicos al dashboard (pueden ver órdenes)
                break;
            } else if (role.equals("ROLE_RECEPCIONISTA")) {
                redirectUrl = "/dashboard"; // Recepcionistas al dashboard
                break;
            } else if (role.equals("ROLE_USER")) {
                redirectUrl = "/cliente-dashboard"; // Clientes a su dashboard personalizado
                break;
            }
        }

        // Redirigir a la URL correspondiente
        response.sendRedirect(redirectUrl);
    }
}
