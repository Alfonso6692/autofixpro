package com.example.autofixpro.enumeration;

public enum Prioridad {
    BAJA("Baja"),
    NORMAL("Normal"),
    ALTA("Alta"),
    URGENTE("Urgente");

    private final String descripcion;

    Prioridad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
