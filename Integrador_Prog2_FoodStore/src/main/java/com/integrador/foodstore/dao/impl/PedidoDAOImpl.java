package com.integrador.foodstore.dao.impl;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.dao.PedidoDAO;
import com.integrador.foodstore.dao.DetallePedidoDAO;
import com.integrador.foodstore.domain.Pedido;
import com.integrador.foodstore.domain.DetallePedido;
import com.integrador.foodstore.domain.Usuario;
import com.integrador.foodstore.enums.Estado;
import com.integrador.foodstore.enums.FormaPago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    private DetallePedidoDAO detalleDAO = new DetallePedidoDAOImpl();

    @Override
    public void guardar(Pedido p) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null; // Declarar fuera del try-with-resources para el finally
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transacción

            // Insertar pedido
            String sqlInsertPedido = "INSERT INTO pedidos (usuario_id, estado, forma_pago, total, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sqlInsertPedido, Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, p.getUsuario().getId()); // Usuario asociado
            ps.setString(2, p.getEstado().name()); // Enum Estado
            ps.setString(3, p.getFormaPago().name()); // Enum FormaPago
            ps.setDouble(4, p.getTotal());
            ps.setBoolean(5, p.isEliminado());
            ps.setTimestamp(6, Timestamp.valueOf(p.getCreatedAt()));
            ps.executeUpdate();

            // Obtener ID generado
            try (ResultSet rs = ps.getGeneratedKeys()) { // try-with-resources para ResultSet
                if (rs.next()) {
                    Long pedidoId = rs.getLong(1);
                    p.setId(pedidoId); // ✅ ahora el pedido tiene ID real

                    // Insertar detalles asociados al pedido
                    if (p.getDetalles() != null) {
                        for (DetallePedido d : p.getDetalles()) {
                            // Asegurarse de que el detalle tenga el pedidoId correcto
                            detalleDAO.guardar(d, pedidoId);
                        }
                    }
                }
            }

            conn.commit(); // Confirmar transacción
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback si falla algo
            }
            throw e;
        } finally {
            if (ps != null) ps.close(); // Cerrar PreparedStatement
            if (conn != null) conn.close(); // Cerrar Connection
        }
    }


    @Override
    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.id, p.eliminado, p.created_at, p.estado, p.forma_pago, p.total, " +
                     "u.id AS usuario_id, u.nombre, u.apellido, u.email " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.eliminado = false";

        try (Connection conn = DatabaseConnection.getConnection(); // try-with-resources para Connection
             PreparedStatement ps = conn.prepareStatement(sql);    // try-with-resources para PreparedStatement
             ResultSet rs = ps.executeQuery()) {                   // try-with-resources para ResultSet

            while (rs.next()) {
                // Crear objeto Usuario desde el JOIN
                Usuario usuario = new Usuario(
                        rs.getLong("usuario_id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email")
                );

                // Crear objeto Pedido con el Usuario cargado
                Pedido p = new Pedido(
                        rs.getLong("id"),
                        rs.getBoolean("eliminado"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        usuario,
                        Estado.valueOf(rs.getString("estado")),
                        FormaPago.valueOf(rs.getString("forma_pago")),
                        rs.getDouble("total")
                );

                pedidos.add(p);
            }
        } // Los recursos (conn, ps, rs) se cierran automáticamente aquí
        return pedidos;
    }


    @Override
    public Pedido buscarPorId(Long id) throws SQLException {
        Pedido pedido = null;
        String sql = "SELECT p.id, p.eliminado, p.created_at, p.estado, p.forma_pago, p.total, " +
                     "u.id AS usuario_id, u.nombre, u.apellido, u.email " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.id = ? AND p.eliminado = false";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { // try-with-resources para ResultSet
                if (rs.next()) {
                    // Crear objeto Usuario desde el JOIN
                    Usuario usuario = new Usuario(
                            rs.getLong("usuario_id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email")
                    );

                    // Crear objeto Pedido con el Usuario cargado
                    pedido = new Pedido(
                            rs.getLong("id"),
                            rs.getBoolean("eliminado"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            usuario,
                            Estado.valueOf(rs.getString("estado")),
                            FormaPago.valueOf(rs.getString("forma_pago")),
                            rs.getDouble("total")
                    );
                }
            }
        }
        return pedido;
    }


    @Override
    public void actualizar(Pedido p) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ?, forma_pago = ?, total = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getEstado().name());
            ps.setString(2, p.getFormaPago().name());
            ps.setDouble(3, p.getTotal());
            ps.setLong(4, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "UPDATE pedidos SET eliminado = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
