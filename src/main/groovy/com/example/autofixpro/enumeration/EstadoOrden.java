package com.example.autofixpro.enumeration;

public enum EstadoOrden {
    RECIBIDO("Recibido"),
    EN_DIAGNOSTICO("En Diagnostico"),
    EN_REPARACION("En Reparación"),
    EN_PRUEBAS("En Pruebas"),
    COMPLETADO("Completado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private String descripcion;
    EstadoOrden(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return descripcion;
    }


}
