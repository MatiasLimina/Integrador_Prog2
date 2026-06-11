package com.integrador.foodstore.domain;

import com.integrador.foodstore.enums.Estado;
import com.integrador.foodstore.enums.FormaPago;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido extends Base{
    private Usuario usuario;
    private Estado estado; // Enum: PENDIENTE, PREPARACION, ENVIADO, ENTREGADO, CANCELADO
    private FormaPago formaPago; // Enum: EFECTIVO, TARJETA, TRANSFERENCIA
    private Double total;
    private List<DetallePedido> detalles; // Relación 1:N con sus renglones

    public Pedido() {
        super();
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    // Constructor para nuevos pedidos
    public Pedido(Usuario usuario, Estado estado, FormaPago formaPago) {
        this();
        this.usuario = usuario;
        this.estado = estado;
        this.formaPago = formaPago;
    }

    // Constructor completo para el DAO
    public Pedido(Long id, boolean eliminado, LocalDateTime createdAt, Usuario usuario, Estado estado, FormaPago formaPago, Double total) {
        super(id, eliminado, createdAt);
        this.usuario = usuario;
        this.estado = estado;
        this.formaPago = formaPago;
        this.total = total;
        this.detalles = new ArrayList<>();
    }

    // MÉTODO OBLIGATORIO SEGÚN LA CONSIGNA: Agrega un detalle y acumula el total
    public void addDetallePedido(DetallePedido detalle) {
        this.detalles.add(detalle);
        // Al agregar, vinculamos bidireccionalmente el detalle con este pedido
        detalle.setPedido(this);
        // Sumamos el subtotal del detalle al total general del pedido
        this.total += detalle.getSubtotal();
    }

    // Getters y Setters
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Pedido [ID: " + getId() + " | Cliente: " + usuario.getApellido() + " | Estado: " + estado + " | Total: $" + total + "]";
    }
}
