package com.integrador.foodstore.dao.impl;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.dao.CategoriaDAO;
import com.integrador.foodstore.domain.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public void guardar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categorias (nombre, descripcion, eliminado, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBoolean(3, c.isEliminado());
            ps.setTimestamp(4, Timestamp.valueOf(c.getCreatedAt()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public List<Categoria> listar() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        }
        return categorias;
    }

    @Override
    public Categoria buscarPorId(Long id) throws SQLException {
        Categoria categoria = null;
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE id = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(
                            rs.getLong("id"),
                            rs.getBoolean("eliminado"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        }
        return categoria;
    }

    @Override
    public void actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setLong(3, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "UPDATE categorias SET eliminado = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Categoria buscarPorNombre(String nombre) throws SQLException {
        Categoria categoria = null;
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categorias WHERE nombre = ? AND eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(
                            rs.getLong("id"),
                            rs.getBoolean("eliminado"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        }
        return categoria;
    }
}