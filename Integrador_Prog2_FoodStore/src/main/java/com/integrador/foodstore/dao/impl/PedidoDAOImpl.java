package com.integrador.foodstore.dao.impl;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.dao.PedidoDAO;
import com.integrador.foodstore.dao.DetallePedidoDAO;
import com.integrador.foodstore.domain.Pedido;
import com.integrador.foodstore.domain.DetallePedido;
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
                    "INSERT INTO pedido (usuario_id, estado, forma_pago, total, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, p.getUsuario().getId());
            ps.setString(2, p.getEstado().name());
            ps.setString(3, p.getFormaPago().name());
            ps.setDouble(4, p.getTotal());
            ps.setBoolean(5, p.isEliminado());
            ps.setTimestamp(6, Timestamp.valueOf(p.getCreatedAt()));
            ps.executeUpdate();

            // Obtener ID generado
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long pedidoId = rs.getLong(1);

                // Insertar detalles
                for (DetallePedido d : p.getDetalles()) {
                    detalleDAO.guardar(d, pedidoId);
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
                "SELECT * FROM pedido WHERE eliminado = false"
        );
        ResultSet rs = ps.executeQuery();

        List<Pedido> pedidos = new ArrayList<>();
        while (rs.next()) {
            Pedido p = new Pedido(
                    rs.getLong("id"),
                    rs.getBoolean("eliminado"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    null, // Usuario se carga aparte
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
                "SELECT * FROM pedido WHERE id = ? AND eliminado = false"
        );
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        Pedido p = null;
        if (rs.next()) {
            p = new Pedido(
                    rs.getLong("id"),
                    rs.getBoolean("eliminado"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    null, // Usuario se carga aparte
                    Estado.valueOf(rs.getString("estado")),
                    FormaPago.valueOf(rs.getString("forma_pago")),
                    rs.getDouble("total")
            );
            // Cargar detalles asociados
            p.setDetalles(detalleDAO.listarPorPedido(id));
        }
        conn.close();
        return p;
    }

    @Override
    public void actualizar(Pedido p) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE pedido SET estado = ?, forma_pago = ?, total = ? WHERE id = ?"
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
                "UPDATE pedido SET eliminado = true WHERE id = ?"
        );
        ps.setLong(1, id);
        ps.executeUpdate();
        conn.close();
    }
}