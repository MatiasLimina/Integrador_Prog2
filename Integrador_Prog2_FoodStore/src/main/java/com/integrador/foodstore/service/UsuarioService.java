package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.UsuarioDAO;
import com.integrador.foodstore.dao.impl.UsuarioDAOImpl;
import com.integrador.foodstore.domain.Usuario;
import com.integrador.foodstore.exception.CamposVaciosException;
import com.integrador.foodstore.exception.EmailDuplicadoException;
import com.integrador.foodstore.exception.ServiceException;
import com.integrador.foodstore.exception.UsuarioNoEncontradoException;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO dao = new UsuarioDAOImpl();

    private void validarCampos(Usuario u) {
        if (u.getNombre() == null || u.getNombre().trim().isEmpty() ||
                u.getApellido() == null || u.getApellido().trim().isEmpty() ||
                u.getEmail() == null || u.getEmail().trim().isEmpty() ||
                u.getCelular() == null || u.getCelular().trim().isEmpty() ||
                u.getPassword() == null || u.getPassword().trim().isEmpty() ||
                u.getRol() == null){
            throw new CamposVaciosException("Error de validación: Ninguno de los campos del formulario puede quedar vacío.");
        }
    }

    public void registrarUsuario(Usuario u) throws CamposVaciosException, EmailDuplicadoException, ServiceException { // Excepciones específicas
        validarCampos(u);

        try {
            if (dao.buscarPorEmail(u.getEmail()) != null) {
                throw new EmailDuplicadoException("El correo electrónico '" + u.getEmail() + "' ya está asociado a otra cuenta.");
            }

            dao.guardar(u);
        } catch (SQLException e) {
            throw new ServiceException("Error al registrar el usuario en la base de datos: " + e.getMessage(), e);
        }
    }

    public void modificarUsuario(Usuario u) throws UsuarioNoEncontradoException, CamposVaciosException, EmailDuplicadoException, ServiceException { // Excepciones específicas
        try {
            if (u.getId() == null || dao.buscarPorId(u.getId()) == null) {
                throw new UsuarioNoEncontradoException("No se pudo realizar la operación: El usuario con ID " + u.getId() + " no existe o ya fue dado de baja.");
            }
            validarCampos(u);

            Usuario existente = dao.buscarPorEmail(u.getEmail());
            if (existente != null && !existente.getId().equals(u.getId())) {
                throw new EmailDuplicadoException("El correo electrónico '" + u.getEmail() + "' ya está asociado a otra cuenta.");
            }

            dao.actualizar(u);
        } catch (SQLException e) {
            throw new ServiceException("Error al modificar el usuario en la base de datos: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarUsuarios() throws ServiceException { // Lanza ServiceException para SQLException
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new ServiceException("Error al listar los usuarios desde la base de datos: " + e.getMessage(), e);
        }
    }

    public Usuario buscarUsuario(Long id) throws ServiceException { // Lanza ServiceException para SQLException
        try {
            return dao.buscarPorId(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al buscar el usuario por ID en la base de datos: " + e.getMessage(), e);
        }
    }

    public void eliminarUsuario(Long id) throws UsuarioNoEncontradoException, ServiceException { // Excepciones específicas
        try {
            if (dao.buscarPorId(id) == null) {
                throw new UsuarioNoEncontradoException("No se pudo realizar la operación: El usuario con ID " + id + " no existe o ya fue dado de baja.");
            }
            dao.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al eliminar el usuario de la base de datos: " + e.getMessage(), e);
        }
    }
}