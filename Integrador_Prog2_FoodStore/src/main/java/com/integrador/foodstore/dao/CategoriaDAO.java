package com.integrador.foodstore.dao;

import com.integrador.foodstore.domain.Categoria;
import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO {
    void guardar(Categoria c) throws SQLException;
    List<Categoria> listar() throws SQLException;
    Categoria buscarPorId(Long id) throws SQLException;
    void actualizar(Categoria c) throws SQLException;
    void eliminar(Long id) throws SQLException; // Baja lógica
    Categoria buscarPorNombre(String nombre) throws SQLException; // Para validar unicidad
}
