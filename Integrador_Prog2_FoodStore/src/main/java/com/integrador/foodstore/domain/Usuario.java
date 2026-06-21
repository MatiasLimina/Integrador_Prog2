package com.integrador.foodstore.domain;

import com.integrador.foodstore.enums.Rol;

import java.time.LocalDateTime;

public class Usuario extends Base{
    private String nombre;
    private String apellido;
    private String email;
    private String celular;
    private String password;
    private Rol rol; // Enum: CLIENTE, OPERADOR, ADMINISTRADOR, etc.

    // Constructor vacío
    public Usuario() {
        super();
    }

    // Constructor para nuevos usuarios (Consola)
    public Usuario(String nombre, String apellido, String email, String celular, String password, Rol rol) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.celular = celular;
        this.password = password;
        this.rol = rol;
    }

    // Constructor completo para el DAO (Base de datos)
    public Usuario(Long id, boolean eliminado, LocalDateTime createdAt, String nombre, String apellido, String email, String celular, String password, Rol rol) {
        super(id, eliminado, createdAt);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.celular = celular;
        this.password = password;
        this.rol = rol;
    }

    // Constructor simplificado para mapear datos desde la tabla pedidos (JOIN con usuarios)
// Se usa en PedidoDAOImpl.listar() y buscarPorId()
    public Usuario(Long id, String nombre, String apellido, String email) {
        super(); // inicializa Base sin datos
        this.setId(id);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }


    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "Usuario [ID: " + getId() + " | " + apellido + ", " + nombre + " | Email: " + email + " | Celular: " + celular + " | Rol: " + rol + "]";
    }
}