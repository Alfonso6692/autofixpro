package com.example.autofixpro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.concurrent.CompletableFuture;

/**
 * Servicio para interactuar con AWS SNS (Simple Notification Service).
 * Permite enviar notificaciones SMS y publicar mensajes en topics de SNS.
 * Las operaciones son asíncronas y se ejecutan en un hilo separado.
 */
@Service
public class AwsSnsService {

    private static final Logger logger = LoggerFactory.getLogger(AwsSnsService.class);

    @Autowired
    private SnsClient snsClient;

    @Value("${aws.sns.enabled:true}")
    private boolean snsEnabled;

    @Value("${aws.sns.topic.autofixpro:}")
    private String autoFixProTopicArn;

    /**
     * Envía una notificación SMS a un número de teléfono específico.
     * Si SNS está deshabilitado, simula el envío.
     *
     * @param numeroTelefono El número de teléfono del destinatario.
     * @param mensaje        El contenido del mensaje.
     * @return Un CompletableFuture con el ID del mensaje de SNS o un ID simulado.
     */
    public CompletableFuture<String> enviarNotificacionSMS(String numeroTelefono, String mensaje) {
        if (!snsEnabled) {
            logger.info("SNS está deshabilitado. SMS simulado enviado a: {} - Mensaje: {}", numeroTelefono, mensaje);
            return CompletableFuture.completedFuture("SMS_SIMULADO");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Formatear número de teléfono para SNS (formato E.164)
                String numeroFormateado = formatearNumeroTelefono(numeroTelefono);

                PublishRequest request = PublishRequest.builder()
                        .phoneNumber(numeroFormateado)
                        .message(mensaje)
                        .build();

                PublishResponse response = snsClient.publish(request);
                logger.info("SMS enviado exitosamente. MessageId: {} - Teléfono: {}",
                           response.messageId(), numeroFormateado);
                return response.messageId();

            } catch (SnsException e) {
                logger.error("Error enviando SMS a {}: {}", numeroTelefono, e.getMessage());
                throw new RuntimeException("Error enviando SMS: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Publica un mensaje en el topic principal de la aplicación.
     * Si SNS o el topic están deshabilitados, simula la publicación.
     *
     * @param mensaje El mensaje a publicar.
     * @param asunto  El asunto del mensaje.
     * @return Un CompletableFuture con el ID del mensaje de SNS o un ID simulado.
     */
    public CompletableFuture<String> publicarEnTopic(String mensaje, String asunto) {
        if (!snsEnabled || autoFixProTopicArn == null || autoFixProTopicArn.isEmpty()) {
            logger.info("SNS/Topic deshabilitado. Notificación simulada - Asunto: {} - Mensaje: {}", asunto, mensaje);
            return CompletableFuture.completedFuture("TOPIC_SIMULADO");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                PublishRequest request = PublishRequest.builder()
                        .topicArn(autoFixProTopicArn)
                        .message(mensaje)
                        .subject(asunto)
                        .build();

                PublishResponse response = snsClient.publish(request);
                logger.info("Mensaje publicado en topic. MessageId: {} - Topic: {}",
                           response.messageId(), autoFixProTopicArn);
                return response.messageId();

            } catch (SnsException e) {
                logger.error("Error publicando en topic {}: {}", autoFixProTopicArn, e.getMessage());
                throw new RuntimeException("Error publicando en topic: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Crea un nuevo topic de SNS.
     * Si SNS está deshabilitado, simula la creación.
     *
     * @param nombreTopic El nombre del topic a crear.
     * @return Un CompletableFuture con el ARN del topic creado o uno simulado.
     */
    public CompletableFuture<String> crearTopic(String nombreTopic) {
        if (!snsEnabled) {
            logger.info("SNS deshabilitado. Topic simulado creado: {}", nombreTopic);
            return CompletableFuture.completedFuture("arn:aws:sns:us-east-2:123456789012:" + nombreTopic);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                CreateTopicRequest request = CreateTopicRequest.builder()
                        .name(nombreTopic)
                        .build();

                CreateTopicResponse response = snsClient.createTopic(request);
                logger.info("Topic creado exitosamente: {} - ARN: {}", nombreTopic, response.topicArn());
                return response.topicArn();

            } catch (SnsException e) {
                logger.error("Error creando topic {}: {}", nombreTopic, e.getMessage());
                throw new RuntimeException("Error creando topic: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Suscribe un endpoint de correo electrónico a un topic de SNS.
     * Si SNS está deshabilitado, simula la suscripción.
     *
     * @param topicArn El ARN del topic.
     * @param email    El correo electrónico a suscribir.
     * @return Un CompletableFuture con el ARN de la suscripción o uno simulado.
     */
    public CompletableFuture<String> suscribirEmail(String topicArn, String email) {
        if (!snsEnabled) {
            logger.info("SNS deshabilitado. Suscripción simulada de email {} al topic {}", email, topicArn);
            return CompletableFuture.completedFuture("SUSCRIPCION_SIMULADA");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                SubscribeRequest request = SubscribeRequest.builder()
                        .topicArn(topicArn)
                        .protocol("email")
                        .endpoint(email)
                        .build();

                SubscribeResponse response = snsClient.subscribe(request);
                logger.info("Email suscrito al topic. SubscriptionArn: {} - Email: {} - Topic: {}",
                           response.subscriptionArn(), email, topicArn);
                return response.subscriptionArn();

            } catch (SnsException e) {
                logger.error("Error suscribiendo email {} al topic {}: {}", email, topicArn, e.getMessage());
                throw new RuntimeException("Error suscribiendo email: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Formatea un número de teléfono al estándar E.164 requerido por AWS SNS.
     * Añade el prefijo de Perú (+51) si no está presente.
     *
     * @param numero El número de teléfono a formatear.
     * @return El número de teléfono en formato E.164.
     */
    private String formatearNumeroTelefono(String numero) {
        // Remover espacios y caracteres especiales
        String numeroLimpio = numero.replaceAll("[^\\d+]", "");

        // Si no empieza con +, asumir código de país de Perú (+51)
        if (!numeroLimpio.startsWith("+")) {
            if (numeroLimpio.startsWith("51")) {
                numeroLimpio = "+" + numeroLimpio;
            } else {
                numeroLimpio = "+51" + numeroLimpio;
            }
        }

        return numeroLimpio;
    }

    /**
     * Verifica si el servicio SNS está habilitado en la configuración.
     * @return true si está habilitado, false en caso contrario.
     */
    public boolean esSnsHabilitado() {
        return snsEnabled;
    }

    /**
     * Obtiene el ARN del topic principal de la aplicación.
     * @return El ARN del topic.
     */
    public String obtenerTopicArn() {
        return autoFixProTopicArn;
    }
}