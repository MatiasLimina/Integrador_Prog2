package com.integrador.foodstore.domain;

import java.time.LocalDateTime;

public class DetallePedido extends Base{
    private Pedido pedido; // Relación N:1 apuntando al pedido padre
    private Producto producto; // Relación N:1 con el artículo del menú
    private int cantidad;
    private Double subtotal;

    public DetallePedido() {
        super();
    }

    // Constructor para armar el renglón desde el menú de compras
    public DetallePedido(Producto producto, int cantidad) {
        super();
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad; // Cálculo automático del subtotal
    }

    // Constructor completo para el DAO
    public DetallePedido(Long id, boolean eliminado, LocalDateTime createdAt, Pedido pedido, Producto producto, int cantidad, Double subtotal) {
        super(id, eliminado, createdAt);
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            this.subtotal = producto.getPrecio() * this.cantidad;
        }
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        if (this.producto != null) {
            this.subtotal = this.producto.getPrecio() * cantidad;
        }
    }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "  - " + producto.getNombre() + " x" + cantidad + " | Subtotal: $" + subtotal;
    }
}
