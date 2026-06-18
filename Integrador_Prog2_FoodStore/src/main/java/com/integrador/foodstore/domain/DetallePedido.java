package com.integrador.foodstore.domain;

import java.time.LocalDateTime;

public class DetallePedido extends Base {
    private Pedido pedido;       // Relación N:1 con Pedido
    private Producto producto;   // Relación N:1 con Producto
    private int cantidad;
    private Double subtotal;

    // Constructor vacío
    public DetallePedido() {
        super();
    }

    // Constructor para crear un detalle desde el menú
    public DetallePedido(Producto producto, int cantidad) {
        super();
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    // Constructor completo para DAO
    public DetallePedido(Long id, boolean eliminado, LocalDateTime createdAt,
                         Pedido pedido, Producto producto, int cantidad, Double subtotal) {
        super(id, eliminado, createdAt);
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    // --- Getters y Setters ---
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) {
        this.producto = producto;
        this.subtotal = calcularSubtotal();
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    // --- Métodos auxiliares ---
    private Double calcularSubtotal() {
        if (producto != null && cantidad > 0) {
            return producto.getPrecio() * cantidad;
        }
        return 0.0;
    }

    // Soft delete: marca el detalle como eliminado
    public void eliminar() {
        this.setEliminado(true);
    }

    @Override
    public String toString() {
        return "Producto: " + producto.getNombre() +
                " | Cantidad: " + cantidad +
                " | Subtotal: $" + subtotal;
    }
}