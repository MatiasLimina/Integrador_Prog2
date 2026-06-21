package com.integrador.foodstore.dao.impl;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.dao.DetallePedidoDAO;
import com.integrador.foodstore.domain.DetallePedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAOImpl implements DetallePedidoDAO {

    // --- Guardar detalle ---
    // Nota: recibe la Connection abierta por PedidoDAOImpl para que todo quede en la misma transacción
    @Override
    public void guardar(DetallePedido d, Long pedidoId, Connection conn) throws SQLException {
        // Usamos la conexión que ya nos pasan (que ya tiene setAutoCommit(false))
        try (PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?)"
             )) {
            ps.setLong(1, pedidoId);
            ps.setLong(2, d.getProducto().getId());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getSubtotal());
            ps.setBoolean(5, d.isEliminado());
            ps.setTimestamp(6, Timestamp.valueOf(d.getCreatedAt()));
            ps.executeUpdate();
        }
        // No cerramos la conexión aquí, PedidoDAOImpl se encarga de eso.
    }

    // --- Listar detalles por pedido ---
    @Override
    public List<DetallePedido> listarPorPedido(Long pedidoId) throws SQLException {
        List<DetallePedido> detalles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM detalles_pedido WHERE pedido_id = ? AND eliminado = false"
             )) {
            ps.setLong(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido d = new DetallePedido(
                            rs.getLong("id"),
                            rs.getBoolean("eliminado"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            null, // Pedido se setea después
                            null, // Producto se carga con otro DAO
                            rs.getInt("cantidad"),
                            rs.getDouble("subtotal")
                    );
                    detalles.add(d);
                }
            }
        }
        return detalles;
    }

    // --- Eliminar detalle por producto (baja lógica) ---
    @Override
    public void eliminarPorProducto(Long pedidoId, Long productoId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE detalles_pedido SET eliminado = true WHERE pedido_id = ? AND producto_id = ?"
             )) {
            ps.setLong(1, pedidoId);
            ps.setLong(2, productoId);
            ps.executeUpdate();
        }
    }
}