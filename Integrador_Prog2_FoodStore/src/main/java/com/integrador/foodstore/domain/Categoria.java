package com.integrador.foodstore.domain;

public class Categoria extends Base{
    private String nombre;
    private String descripcion;


    public Categoria (){};

    public Categoria(String nombre, String descripcion) {
        super();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Constructor con ID para cuando el DAO recupera los datos existentes de la DB
    public Categoria(Long id, boolean eliminado, java.time.LocalDateTime createdAt, String nombre, String descripcion) {
        super(id, eliminado, createdAt);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Categoría [ID: " + getId()
                + " | Nombre: " + nombre
                + " | Descripción: "
                + descripcion + "]";
    }
}
