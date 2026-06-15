package com.integrador.foodstore.domain;

import com.integrador.foodstore.enums.Estado;
import com.integrador.foodstore.enums.FormaPago;
import com.integrador.foodstore.interfaces.Calculable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pedido extends Base implements Calculable {
    private Usuario usuario;
    private Estado estado;
    private FormaPago formaPago;
    private Double total;
    private List<DetallePedido> detalles;

    // Constructor vacío
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

    // Constructor completo para DAO
    public Pedido(Long id, boolean eliminado, LocalDateTime createdAt,
                  Usuario usuario, Estado estado, FormaPago formaPago, Double total) {
        super(id, eliminado, createdAt);
        this.usuario = usuario;
        this.estado = estado;
        this.formaPago = formaPago;
        this.total = total;
        this.detalles = new ArrayList<>();
    }

    // --- Métodos obligatorios según consigna ---
    public void addDetallePedido(DetallePedido detalle) {
        if (detalle == null || detalle.getCantidad() <= 0 || detalle.getProducto() == null) {
            throw new IllegalArgumentException("Detalle inválido: producto nulo o cantidad <= 0");
        }
        this.detalles.add(detalle);
        detalle.setPedido(this); // vinculación bidireccional
        calcularTotal(); // recalcular total usando la interfaz
    }

    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        for (DetallePedido d : detalles) {
            if (d.getProducto().equals(producto) && !d.isEliminado()) {
                return d;
            }
        }
        return null;
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        Iterator<DetallePedido> it = detalles.iterator();
        while (it.hasNext()) {
            DetallePedido d = it.next();
            if (d.getProducto().equals(producto)) {
                d.eliminar(); // soft delete
            }
        }
        calcularTotal(); // recalcular total después de eliminar
    }

    // Implementación de la interfaz Calculable
    @Override
    public void calcularTotal() {
        this.total = 0.0;
        for (DetallePedido d : detalles) {
            if (!d.isEliminado()) {
                this.total += d.getSubtotal();
            }
        }
    }

    // --- Getters y Setters ---
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

    // Soft delete del pedido
    public void eliminar() {
        this.setEliminado(true);
        for (DetallePedido d : detalles) {
            d.eliminar();
        }
    }

    @Override
    public String toString() {
        return "Pedido [ID: " + getId() +
                " | Cliente: " + usuario.getApellido() +
                " | Estado: " + estado +
                " | Forma de Pago: " + formaPago +
                " | Total: $" + total + "]";
    }
}
