package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.UsuarioDAO;
import com.integrador.foodstore.dao.impl.UsuarioDAOImpl;
import com.integrador.foodstore.domain.Usuario;
import com.integrador.foodstore.exception.CamposVaciosException;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO dao = new UsuarioDAOImpl();
    private void validarCampos(Usuario u) {
        if (u.getNombre() == null || u.getNombre().trim().isEmpty() ||
                u.getApellido() == null || u.getApellido().trim().isEmpty() ||
                u.getEmail() == null || u.getEmail().trim().isEmpty() ||
                u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            throw new CamposVaciosException("Error de validación: Ninguno de los campos del formulario puede quedar vacío.");
        }
    }
    public void registrarUsuario(Usuario u) throws Exception {
        validarCampos(u);

        // Regla de negocio: El email debe ser único (HU-USR-02)
        if (dao.buscarPorEmail(u.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya se encuentra registrado por otro usuario.");
        }

        dao.guardar(u);
    }

    public void modificarUsuario(Usuario u) throws Exception {
        if (u.getId() == null || dao.buscarPorId(u.getId()) == null) {
            throw new Exception("El usuario con el ID especificado no existe o fue eliminado.");
        }
        validarCampos(u);

        // Validar unicidad de mail al editar
        Usuario existente = dao.buscarPorEmail(u.getEmail());
        if (existente != null && !existente.getId().equals(u.getId())) {
            throw new IllegalArgumentException("El email ingresado ya está en uso por otro usuario.");
        }

        dao.actualizar(u);
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        return dao.listar();
    }

    public Usuario buscarUsuario(Long id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void eliminarUsuario(Long id) throws Exception {
        if (dao.buscarPorId(id) == null) {
            throw new Exception("No se encontró un usuario activo con el ID proporcionado.");
        }
        dao.eliminar(id);
    }

}
