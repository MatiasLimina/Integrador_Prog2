package com.integrador.foodstore.domain;

import com.integrador.foodstore.enums.Rol;

import java.time.LocalDateTime;

public class Usuario extends Base{
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Rol rol; // Enum: CLIENTE, OPERADOR, ADMINISTRADOR, etc.

    // Constructor vacío
    public Usuario() {
        super();
    }

    // Constructor para nuevos usuarios (Consola)
    public Usuario(String nombre, String apellido, String email, String password, Rol rol) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Constructor completo para el DAO (Base de datos)
    public Usuario(Long id, boolean eliminado, LocalDateTime createdAt, String nombre, String apellido, String email, String password, Rol rol) {
        super(id, eliminado, createdAt);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "Usuario [ID: " + getId() + " | " + apellido + ", " + nombre + " | Email: " + email + " | Rol: " + rol + "]";
    }
}
