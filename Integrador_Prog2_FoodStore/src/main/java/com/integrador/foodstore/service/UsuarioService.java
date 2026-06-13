package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.UsuarioDAO;
import com.integrador.foodstore.dao.impl.UsuarioDAOImpl;
import com.integrador.foodstore.domain.Usuario;
import com.integrador.foodstore.exception.CamposVaciosException;
import com.integrador.foodstore.exception.EmailDuplicadoException;
import com.integrador.foodstore.exception.UsuarioNoEncontradoException;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO dao = new UsuarioDAOImpl();
    private void validarCampos(Usuario u) {
        if (u.getNombre() == null || u.getNombre().trim().isEmpty() ||
                u.getApellido() == null || u.getApellido().trim().isEmpty() ||
                u.getEmail() == null || u.getEmail().trim().isEmpty() ||
                u.getPassword() == null || u.getPassword().trim().isEmpty() ||
                u.getRol() == null){
            throw new CamposVaciosException("Error de validación: Ninguno de los campos del formulario puede quedar vacío.");
        }
    }
    public void registrarUsuario(Usuario u) throws Exception {
        validarCampos(u);

        // Regla de negocio: El email debe ser único (HU-USR-02)
        if (dao.buscarPorEmail(u.getEmail()) != null) {
            throw new EmailDuplicadoException("El correo electrónico '" + u.getEmail() + "' ya está asociado a otra cuenta.");
        }

        dao.guardar(u);
    }

    public void modificarUsuario(Usuario u) throws Exception {
        if (u.getId() == null || dao.buscarPorId(u.getId()) == null) {
            throw new UsuarioNoEncontradoException("No se pudo realizar la operación: El usuario con ID " + u.getId() + " no existe o ya fue dado de baja.");
        }
        validarCampos(u);

        // Validar unicidad de mail al editar
        Usuario existente = dao.buscarPorEmail(u.getEmail());
        if (existente != null && !existente.getId().equals(u.getId())) {
            throw new EmailDuplicadoException("El correo electrónico '" + u.getEmail() + "' ya está asociado a otra cuenta.");
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
            throw new UsuarioNoEncontradoException("No se pudo realizar la operación: El usuario con ID " + id + " no existe o ya fue dado de baja.");
        }
        dao.eliminar(id);
    }

}
