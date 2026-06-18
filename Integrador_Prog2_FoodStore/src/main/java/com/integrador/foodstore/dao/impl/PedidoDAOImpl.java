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
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transacción

            // Insertar pedido
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pedidos (usuario_id, estado, forma_pago, total, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setLong(1, p.getUsuario().getId()); // Usuario asociado
            ps.setString(2, p.getEstado().name()); // Enum Estado
            ps.setString(3, p.getFormaPago().name()); // Enum FormaPago
            ps.setDouble(4, p.getTotal());
            ps.setBoolean(5, p.isEliminado());
            ps.setTimestamp(6, Timestamp.valueOf(p.getCreatedAt()));
            ps.executeUpdate();

            // Obtener ID generado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long pedidoId = rs.getLong(1);
                    p.setId(pedidoId); // ✅ ahora el pedido tiene ID real

                    // Insertar detalles asociados al pedido
                    if (p.getDetalles() != null) {
                        for (DetallePedido d : p.getDetalles()) {
                            detalleDAO.guardar(d, pedidoId);
                        }
                    }
                }
            }

            conn.commit(); // Confirmar transacción
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback si falla algo
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }


    @Override
    public List<Pedido> listar() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT p.id, p.eliminado, p.created_at, p.estado, p.forma_pago, p.total, " +
                        "u.id AS usuario_id, u.nombre, u.apellido, u.email " +
                        "FROM pedidos p " +
                        "JOIN usuarios u ON p.usuario_id = u.id " +
                        "WHERE p.eliminado = false"
        );
        ResultSet rs = ps.executeQuery();

        List<Pedido> pedidos = new ArrayList<>();
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

        conn.close();
        return pedidos;
    }



    @Override
    public Pedido buscarPorId(Long id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT p.id, p.eliminado, p.created_at, p.estado, p.forma_pago, p.total, " +
                        "u.id AS usuario_id, u.nombre, u.apellido, u.email " +
                        "FROM pedidos p " +
                        "JOIN usuarios u ON p.usuario_id = u.id " +
                        "WHERE p.id = ? AND p.eliminado = false"
        );
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        Pedido pedido = null;
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

        conn.close();
        return pedido;
    }


    @Override
    public void actualizar(Pedido p) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE pedidos SET estado = ?, forma_pago = ?, total = ? WHERE id = ?"
        );
        ps.setString(1, p.getEstado().name());
        ps.setString(2, p.getFormaPago().name());
        ps.setDouble(3, p.getTotal());
        ps.setLong(4, p.getId());
        ps.executeUpdate();
        conn.close();
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE pedidos SET eliminado = true WHERE id = ?"
        );
        ps.setLong(1, id);
        ps.executeUpdate();
        conn.close();
    }
}