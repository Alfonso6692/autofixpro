package com.example.autofixpro.enumeration;

public enum Categoria {
    MECANICA("Mecánica"),
    ELECTRICA("Eléctrica"),
    CARROCERIA("Carrocería"),
    MANTENIMIENTO("Mantenimiento");

    private final String descripcion;

    Categoria(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}