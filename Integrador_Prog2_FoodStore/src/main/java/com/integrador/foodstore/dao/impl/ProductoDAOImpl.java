package com.integrador.foodstore.dao.impl;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.dao.ProductoDAO;
import com.integrador.foodstore.domain.Categoria;
import com.integrador.foodstore.domain.Producto;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public void guardar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, descripcion, stock, imagen, disponible, categoria_id, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImagen());
            ps.setBoolean(6, p.getDisponible());
            ps.setLong(7, p.getCategoria().getId());
            ps.setBoolean(8, p.isEliminado());
            ps.setTimestamp(9, Timestamp.valueOf(p.getCreatedAt()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getLong(1));
                }
            }
        }
    }

    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getLong("categoria_id"),
                rs.getBoolean("cat_eliminado"),
                rs.getTimestamp("cat_created_at").toLocalDateTime(),
                rs.getString("cat_nombre"),
                rs.getString("cat_descripcion")
        );

        return new Producto(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getString("descripcion"),
                rs.getInt("stock"),
                rs.getString("imagen"),
                rs.getBoolean("disponible"),
                categoria
        );
    }

    @Override
    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.eliminado, p.created_at, p.nombre, p.precio, p.descripcion, p.stock, p.imagen, p.disponible, " +
                         "c.id AS categoria_id, c.nombre AS cat_nombre, c.descripcion AS cat_descripcion, c.eliminado AS cat_eliminado, c.created_at AS cat_created_at " +
                         "FROM productos p JOIN categorias c ON p.categoria_id = c.id WHERE p.eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        return productos;
    }

    @Override
    public Producto buscarPorId(Long id) throws SQLException {
        Producto producto = null;
        String sql = "SELECT p.id, p.eliminado, p.created_at, p.nombre, p.precio, p.descripcion, p.stock, p.imagen, p.disponible, " +
                         "c.id AS categoria_id, c.nombre AS cat_nombre, c.descripcion AS cat_descripcion, c.eliminado AS cat_eliminado, c.created_at AS cat_created_at " +
                         "FROM productos p JOIN categorias c ON p.categoria_id = c.id WHERE p.id = ? AND p.eliminado = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = mapResultSetToProducto(rs);
                }
            }
        }
        return producto;
    }

    @Override
    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, descripcion = ?, stock = ?, imagen = ?, disponible = ?, categoria_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImagen());
            ps.setBoolean(6, p.getDisponible());
            ps.setLong(7, p.getCategoria().getId());
            ps.setLong(8, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "UPDATE productos SET eliminado = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Producto> listarPorCategoria(Long categoriaId) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.eliminado, p.created_at, p.nombre, p.precio, p.descripcion, p.stock, p.imagen, p.disponible, " +
                         "c.id AS categoria_id, c.nombre AS cat_nombre, c.descripcion AS cat_descripcion, c.eliminado AS cat_eliminado, c.created_at AS cat_created_at " +
                         "FROM productos p JOIN categorias c ON p.categoria_id = c.id WHERE p.eliminado = false AND p.categoria_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
}