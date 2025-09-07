package com.example.autofixpro.util;

import com.example.autofixpro.entity.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class SistemaNotificaciones {

    // Implementación Singleton
    private static SistemaNotificaciones instance;
    private List<String> configuracionEmail;
    private List<String> configuracionSMS;
    private List<String> plantillas;

    private SistemaNotificaciones() {
        // Configuración inicial
        this.configuracionEmail = new ArrayList<>();
        this.configuracionSMS = new ArrayList<>();
        this.plantillas = new ArrayList<>();
        inicializarConfiguracion();
    }

    public static synchronized SistemaNotificaciones getInstance() {
        if (instance == null) {
            instance = new SistemaNotificaciones();
        }
        return instance;
    }

    private void inicializarConfiguracion() {
        // Configuración de email
        configuracionEmail.add("smtp.host=smtp.gmail.com");
        configuracionEmail.add("smtp.port=587");
        configuracionEmail.add("smtp.auth=true");

        // Configuración de SMS (Twilio)
        configuracionSMS.add("twilio.account.sid=YOUR_ACCOUNT_SID");
        configuracionSMS.add("twilio.auth.token=YOUR_AUTH_TOKEN");
        configuracionSMS.add("twilio.phone.number=+1234567890");

        // Plantillas de notificación
        plantillas.add("INGRESO: Su vehículo {placa} ha ingresado al taller");
        plantillas.add("ACTUALIZACION: Estado actualizado: {estado}");
        plantillas.add("COMPLETADO: Su vehículo está listo para recoger");
    }

    public void enviarNotificacion(String email, Notification notification) {
        // Envío asíncrono de email
        CompletableFuture.runAsync(() -> {
            try {
                // Simular envío de email
                Thread.sleep(1000);
                notification.setFechaEnvio(LocalDateTime.now());
                notification.setCanal("EMAIL");
                System.out.println("Email enviado a: " + email + " - " + notification.getMensajes());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void enviarSMS(String telefono, String mensaje) {
        // Envío asíncrono de SMS
        CompletableFuture.runAsync(() -> {
            try {
                // Simular envío de SMS
                Thread.sleep(500);
                System.out.println("SMS enviado a: " + telefono + " - " + mensaje);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void programarNotificacion(String destinatario, String mensaje, LocalDateTime fechaEnvio) {
        // Programar notificación para fecha específica
        CompletableFuture.runAsync(() -> {
            try {
                long delay = java.time.Duration.between(LocalDateTime.now(), fechaEnvio).toMillis();
                if (delay > 0) {
                    Thread.sleep(delay);
                }
                enviarNotificacion(destinatario, crearNotificacion(mensaje));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public List<String> obtenerHistorial() {
        // Retornar historial de notificaciones enviadas
        return new ArrayList<>(plantillas);
    }

    public void configurarPlantilla(String tipoNotificacion, String plantilla) {
        plantillas.add(tipoNotificacion + ": " + plantilla);
    }

    private Notification crearNotificacion(String mensaje) {
        Notification notification = new Notification();
        notification.setMensajes(mensaje);
        notification.setFechaEnvio(LocalDateTime.now());
        return notification;
    }
}