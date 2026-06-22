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
            conn.setAutoCommit(false);

            String sqlInsertPedido = "INSERT INTO pedidos (usuario_id, fecha, estado, forma_pago, total, eliminado) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, p.getUsuario().getId());
                ps.setDate(2, Date.valueOf(p.getFecha()));
                ps.setString(3, p.getEstado().name());
                ps.setString(4, p.getFormaPago().name());
                ps.setDouble(5, p.getTotal());
                ps.setBoolean(6, p.isEliminado());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long pedidoId = rs.getLong(1);
                        p.setId(pedidoId);

                        if (p.getDetalles() != null) {
                            for (DetallePedido d : p.getDetalles()) {
                                detalleDAO.guardar(d, pedidoId, conn);
                            }
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }


    @Override
    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.id, p.eliminado, p.fecha, p.estado, p.forma_pago, p.total, " +
                     "u.id AS usuario_id, u.nombre, u.apellido, u.email " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.eliminado = false";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getLong("usuario_id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email")
                );

                Pedido p = new Pedido(
                        rs.getLong("id"),
                        rs.getBoolean("eliminado"),
                        null, // createdAt no se usa desde la DB
                        usuario,
                        Estado.valueOf(rs.getString("estado")),
                        FormaPago.valueOf(rs.getString("forma_pago")),
                        rs.getDouble("total"),
                        rs.getDate("fecha").toLocalDate()
                );
                pedidos.add(p);
            }
        }
        return pedidos;
    }


    @Override
    public Pedido buscarPorId(Long id) throws SQLException {
        Pedido pedido = null;
        String sql = "SELECT p.id, p.eliminado, p.fecha, p.estado, p.forma_pago, p.total, " +
                     "u.id AS usuario_id, u.nombre, u.apellido, u.email " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE p.id = ? AND p.eliminado = false";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getLong("usuario_id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email")
                    );

                    pedido = new Pedido(
                            rs.getLong("id"),
                            rs.getBoolean("eliminado"),
                            null, // createdAt no se usa desde la DB
                            usuario,
                            Estado.valueOf(rs.getString("estado")),
                            FormaPago.valueOf(rs.getString("forma_pago")),
                            rs.getDouble("total"),
                            rs.getDate("fecha").toLocalDate()
                    );
                }
            }
        }
        return pedido;
    }


    @Override
    public void actualizar(Pedido p) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ?, forma_pago = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getEstado().name());
            ps.setString(2, p.getFormaPago().name());
            ps.setLong(3, p.getId());
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