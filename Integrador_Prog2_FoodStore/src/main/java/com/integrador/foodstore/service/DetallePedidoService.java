package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.DetallePedidoDAO;
import com.integrador.foodstore.dao.impl.DetallePedidoDAOImpl;
import com.integrador.foodstore.domain.DetallePedido;

import java.sql.SQLException;
import java.util.List;

public class DetallePedidoService {

    private DetallePedidoDAO detalleDAO = new DetallePedidoDAOImpl();

    // Crear un nuevo detalle asociado a un pedido
    public void crearDetalle(DetallePedido detalle, Long pedidoId) {
        try {
            detalleDAO.guardar(detalle, pedidoId);
            System.out.println("✅ Detalle agregado correctamente al pedido " + pedidoId);
        } catch (SQLException e) {
            System.err.println("❌ Error al agregar detalle: " + e.getMessage());
        }
    }

    // Listar todos los detalles de un pedido
    public void listarDetallesPorPedido(Long pedidoId) {
        try {
            List<DetallePedido> detalles = detalleDAO.listarPorPedido(pedidoId);
            if (detalles.isEmpty()) {
                System.out.println("No hay detalles para el pedido con ID: " + pedidoId);
            } else {
                for (DetallePedido d : detalles) {
                    System.out.println(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar detalles: " + e.getMessage());
        }
    }

    // Eliminar (baja lógica) un detalle por producto
    public void eliminarDetallePorProducto(Long pedidoId, Long productoId) {
        try {
            detalleDAO.eliminarPorProducto(pedidoId, productoId);
            System.out.println("✅ Detalle eliminado correctamente del pedido " + pedidoId);
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar detalle: " + e.getMessage());
        }
    }
}
