package com.integrador.foodstore.dao;

import com.integrador.foodstore.domain.Producto;
import java.sql.SQLException;
import java.util.List;

public interface ProductoDAO {
    void guardar(Producto p) throws SQLException;
    List<Producto> listar() throws SQLException;
    Producto buscarPorId(Long id) throws SQLException;
    void actualizar(Producto p) throws SQLException;
    void eliminar(Long id) throws SQLException; // Baja lógica
    List<Producto> listarPorCategoria(Long categoriaId) throws SQLException; // Opcional según consigna
}
