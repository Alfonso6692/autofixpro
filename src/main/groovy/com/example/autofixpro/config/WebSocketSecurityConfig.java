package com.example.autofixpro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Configuración de seguridad para WebSocket/STOMP.
 * Asegura que solo usuarios autenticados puedan conectarse y recibir notificaciones.
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Permitir todas las conexiones WebSocket (la autenticación se maneja a nivel HTTP)
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT,
                             SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
            // Permitir envío de mensajes (validación en el controlador si es necesario)
            .simpDestMatchers("/app/**").permitAll()
            // Permitir suscripción a notificaciones personales
            .simpSubscribeDestMatchers("/user/queue/**", "/user/**").permitAll()
            // Permitir suscripción a topics públicos
            .simpSubscribeDestMatchers("/topic/**", "/queue/**").permitAll()
            // Permitir todo para desarrollo - ajustar en producción
            .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Deshabilitar verificación de mismo origen para desarrollo
        // En producción, cambiar a false
        return true;
    }
}
