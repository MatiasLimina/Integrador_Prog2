package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.CategoriaDAO;
import com.integrador.foodstore.dao.impl.CategoriaDAOImpl;
import com.integrador.foodstore.domain.Categoria;
import com.integrador.foodstore.exception.ServiceException; // Necesitaremos una excepción personalizada

import java.sql.SQLException;
import java.util.List;

public class CategoriaService {

    private CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    public void guardarCategoria(Categoria categoria) throws ServiceException {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new ServiceException("El nombre de la categoría no puede estar vacío.");
        }
        if (categoria.getDescripcion() == null || categoria.getDescripcion().trim().isEmpty()) {
            throw new ServiceException("La descripción de la categoría no puede estar vacía.");
        }

        try {
            // Validar unicidad del nombre
            Categoria categoriaExistente = categoriaDAO.buscarPorNombre(categoria.getNombre());
            if (categoriaExistente != null && (categoria.getId() == null || !categoriaExistente.getId().equals(categoria.getId()))) {
                throw new ServiceException("Ya existe una categoría con el nombre '" + categoria.getNombre() + "'.");
            }
            categoriaDAO.guardar(categoria);
        } catch (SQLException e) {
            throw new ServiceException("Error al guardar la categoría en la base de datos: " + e.getMessage(), e);
        }
    }

    public List<Categoria> listarCategorias() throws ServiceException {
        try {
            return categoriaDAO.listar();
        } catch (SQLException e) {
            throw new ServiceException("Error al listar las categorías desde la base de datos: " + e.getMessage(), e);
        }
    }

    public Categoria buscarCategoriaPorId(Long id) throws ServiceException {
        try {
            return categoriaDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al buscar la categoría por ID en la base de datos: " + e.getMessage(), e);
        }
    }

    public void actualizarCategoria(Categoria categoria) throws ServiceException {
        if (categoria.getId() == null) {
            throw new ServiceException("El ID de la categoría no puede ser nulo para actualizar.");
        }
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new ServiceException("El nombre de la categoría no puede estar vacío.");
        }
        if (categoria.getDescripcion() == null || categoria.getDescripcion().trim().isEmpty()) {
            throw new ServiceException("La descripción de la categoría no puede estar vacía.");
        }

        try {
            // Validar unicidad del nombre (excluyendo la propia categoría si se está actualizando)
            Categoria categoriaExistente = categoriaDAO.buscarPorNombre(categoria.getNombre());
            if (categoriaExistente != null && !categoriaExistente.getId().equals(categoria.getId())) {
                throw new ServiceException("Ya existe otra categoría con el nombre '" + categoria.getNombre() + "'.");
            }

            // Verificar que la categoría a actualizar realmente existe y no está eliminada
            Categoria categoriaActual = categoriaDAO.buscarPorId(categoria.getId());
            if (categoriaActual == null) {
                throw new ServiceException("La categoría con ID " + categoria.getId() + " no existe o está eliminada.");
            }

            categoriaDAO.actualizar(categoria);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar la categoría en la base de datos: " + e.getMessage(), e);
        }
    }

    public void eliminarCategoria(Long id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("El ID de la categoría no puede ser nulo para eliminar.");
        }
        try {
            // Verificar si la categoría tiene productos asociados antes de eliminar.
            // Por ahora, solo eliminamos lógicamente.
            // Si se quisiera impedir la eliminación, llamar a un ProductoDAO para verificar.

            Categoria categoriaAEliminar = categoriaDAO.buscarPorId(id);
            if (categoriaAEliminar == null) {
                throw new ServiceException("La categoría con ID " + id + " no existe o ya está eliminada.");
            }

            categoriaDAO.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al eliminar la categoría de la base de datos: " + e.getMessage(), e);
        }
    }
}
