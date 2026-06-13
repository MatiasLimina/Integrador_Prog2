package com.integrador.foodstore.dao;

import com.integrador.foodstore.domain.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    void guardar(Usuario u) throws SQLException;
    List<Usuario> listar() throws SQLException;
    Usuario buscarPorId(Long id) throws SQLException;
    Usuario buscarPorEmail(String email) throws SQLException; // Útil para validar unicidad
    void actualizar(Usuario u) throws SQLException;
    void eliminar(Long id) throws SQLException; // Será baja lógica
}
