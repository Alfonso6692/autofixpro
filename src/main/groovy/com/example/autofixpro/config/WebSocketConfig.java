package com.example.autofixpro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para notificaciones en tiempo real.
 * Permite a los clientes recibir actualizaciones del estado de sus vehículos
 * sin necesidad de refrescar la página.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configurar el broker de mensajes STOMP.
     * - /topic: para mensajes broadcast (un mensaje a muchos usuarios)
     * - /queue: para mensajes punto a punto (un mensaje a un usuario específico)
     * - /app: prefijo para mensajes dirigidos a métodos @MessageMapping
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilitar un broker simple en memoria
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefijo para destinos de aplicación
        registry.setApplicationDestinationPrefixes("/app");

        // Prefijo para mensajes dirigidos a usuarios específicos
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Registrar endpoints STOMP.
     * Los clientes se conectarán a /ws-notifications para establecer la conexión WebSocket.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket con fallback SockJS para navegadores que no soportan WebSocket
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Endpoint sin SockJS para clientes que soportan WebSocket nativo
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*");
    }
}
