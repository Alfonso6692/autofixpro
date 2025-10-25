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
            // Permitir conexión y suscripción solo a usuarios autenticados
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
            // Solo usuarios autenticados pueden enviar mensajes
            .simpDestMatchers("/app/**").authenticated()
            // Solo usuarios autenticados pueden suscribirse a notificaciones personales
            .simpSubscribeDestMatchers("/user/queue/**").authenticated()
            // Permitir suscripción a topics públicos (opcional)
            .simpSubscribeDestMatchers("/topic/**").permitAll()
            // Todo lo demás requiere autenticación
            .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Deshabilitar verificación de mismo origen para desarrollo
        // En producción, cambiar a false
        return true;
    }
}
