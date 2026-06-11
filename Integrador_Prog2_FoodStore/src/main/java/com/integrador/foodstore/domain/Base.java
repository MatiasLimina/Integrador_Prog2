package com.integrador.foodstore.domain;

import java.time.LocalDateTime;

public class Base {
    private Long id; // Corresponderá al ID (PRIMARY KEY AUTO_INCREMENT) de la tabla
    private boolean eliminado; // Para el soft delete (baja lógica), mapeado como BIT o TINYINT(1)
    private LocalDateTime createdAt; // Mapeado como DATETIME o TIMESTAMP en SQL

    // Constructor para cuando creamos un objeto nuevo que todavía no se guardó en la DB (el ID lo genera SQL)
    public Base() {
        this.id = null;
        this.eliminado = false; // Por defecto nace activo
        this.createdAt = LocalDateTime.now(); // Fecha y hora del sistema al instanciar
    }

    // Constructor para cuando recuperamos registros existentes desde la Base de Datos (vía el DAO)
    public Base(Long id, boolean eliminado, LocalDateTime createdAt) {
        this.id = id;
        this.eliminado = eliminado;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Sobrescritura de equals y hashCode por ID (indispensable para comparar entidades persistidas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base base = (Base) o;
        return id != null && id.equals(base.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
