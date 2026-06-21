package com.integrador.foodstore.dao.impl;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.dao.CategoriaDAO;
import com.integrador.foodstore.domain.Categoria;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public void guardar(Categoria c) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO categorias (nombre, descripcion, eliminado, created_at) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBoolean(3, c.isEliminado());
            ps.setTimestamp(4, Timestamp.valueOf(c.getCreatedAt()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getLong(1));
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    @Override
    public List<Categoria> listar() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Categoria> categorias = new ArrayList<>();
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE eliminado = false";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Categoria c = new Categoria(
                        rs.getLong("id"),
                        rs.getBoolean("eliminado"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                categorias.add(c);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
        return categorias;
    }

    @Override
    public Categoria buscarPorId(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Categoria categoria = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE id = ? AND eliminado = false";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                categoria = new Categoria(
                        rs.getLong("id"),
                        rs.getBoolean("eliminado"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
        return categoria;
    }

    @Override
    public void actualizar(Categoria c) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setLong(3, c.getId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE categorias SET eliminado = true WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    @Override
    public Categoria buscarPorNombre(String nombre) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Categoria categoria = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE nombre = ? AND eliminado = false";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();

            if (rs.next()) {
                categoria = new Categoria(
                        rs.getLong("id"),
                        rs.getBoolean("eliminado"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
        return categoria;
    }
}
