package com.example.autofixpro.enumeration;

public enum TipoNotificacion {
    INGRESO("Ingreso de Vehículo"),
    ACTUALIZACION("Actualización de Estado"),
    COMPLETADO("Servicio Completado"),
    RECORDATORIO("Recordatorio");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return descripcion;
    }
}
