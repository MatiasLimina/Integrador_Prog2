package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.CategoriaDAO;
import com.integrador.foodstore.dao.ProductoDAO;
import com.integrador.foodstore.dao.impl.CategoriaDAOImpl;
import com.integrador.foodstore.dao.impl.ProductoDAOImpl;
import com.integrador.foodstore.domain.Categoria;
import com.integrador.foodstore.domain.Producto;
import com.integrador.foodstore.exception.ServiceException;

import java.sql.SQLException;
import java.util.List;

public class ProductoService {

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO; // Necesario para validar la categoría asociada

    public ProductoService() {
        this.productoDAO = new ProductoDAOImpl();
        this.categoriaDAO = new CategoriaDAOImpl(); // Inicializar para validar categorías
    }

    public void guardarProducto(Producto producto) throws ServiceException {
        // Validaciones de negocio según consignas
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new ServiceException("El nombre del producto no puede estar vacío.");
        }
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new ServiceException("El precio del producto no puede ser negativo.");
        }
        if (producto.getStock() < 0) {
            throw new ServiceException("El stock del producto no puede ser negativo.");
        }
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new ServiceException("El producto debe tener una categoría asociada.");
        }

        try {
            // Validar que la categoría asociada exista y no esté eliminada
            Categoria categoriaExistente = categoriaDAO.buscarPorId(producto.getCategoria().getId());
            if (categoriaExistente == null) {
                throw new ServiceException("La categoría con ID " + producto.getCategoria().getId() + " no existe o está eliminada.");
            }
            // Asegurar que el objeto Categoria en Producto sea el cargado de la DB
            producto.setCategoria(categoriaExistente);

            productoDAO.guardar(producto);
        } catch (SQLException e) {
            throw new ServiceException("Error al guardar el producto en la base de datos: " + e.getMessage(), e);
        }
    }

    public List<Producto> listarProductos() throws ServiceException {
        try {
            return productoDAO.listar();
        } catch (SQLException e) {
            throw new ServiceException("Error al listar los productos desde la base de datos: " + e.getMessage(), e);
        }
    }

    public Producto buscarProductoPorId(Long id) throws ServiceException {
        try {
            return productoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al buscar el producto por ID en la base de datos: " + e.getMessage(), e);
        }
    }

    public void actualizarProducto(Producto producto) throws ServiceException {
        if (producto.getId() == null) {
            throw new ServiceException("El ID del producto no puede ser nulo para actualizar.");
        }
        // Validaciones de negocio
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new ServiceException("El nombre del producto no puede estar vacío.");
        }
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new ServiceException("El precio del producto no puede ser negativo.");
        }
        if (producto.getStock() < 0) {
            throw new ServiceException("El stock del producto no puede ser negativo.");
        }
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new ServiceException("El producto debe tener una categoría asociada.");
        }

        try {
            // Verificar que el producto a actualizar realmente existe y no está eliminado
            Producto productoActual = productoDAO.buscarPorId(producto.getId());
            if (productoActual == null) {
                throw new ServiceException("El producto con ID " + producto.getId() + " no existe o está eliminado.");
            }

            // Validar que la nueva categoría asociada exista y no esté eliminada
            Categoria categoriaExistente = categoriaDAO.buscarPorId(producto.getCategoria().getId());
            if (categoriaExistente == null) {
                throw new ServiceException("La categoría con ID " + producto.getCategoria().getId() + " no existe o está eliminada.");
            }
            // Asegurar que el objeto Categoria en Producto sea el cargado de la DB
            producto.setCategoria(categoriaExistente);

            productoDAO.actualizar(producto);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar el producto en la base de datos: " + e.getMessage(), e);
        }
    }

    public void eliminarProducto(Long id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("El ID del producto no puede ser nulo para eliminar.");
        }
        try {
            // Verificar que el producto a eliminar realmente existe y no está eliminado
            Producto productoAEliminar = productoDAO.buscarPorId(id);
            if (productoAEliminar == null) {
                throw new ServiceException("El producto con ID " + id + " no existe o ya está eliminado.");
            }
            // Verificar si el producto está referenciado en detalles de pedidos
            // Como estamos haciendo soft delete, la integridad se mantiene.

            productoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al eliminar el producto de la base de datos: " + e.getMessage(), e);
        }
    }

    public List<Producto> listarProductosPorCategoria(Long categoriaId) throws ServiceException {
        if (categoriaId == null) {
            throw new ServiceException("El ID de la categoría no puede ser nulo para listar productos.");
        }
        try {
            // Validar que la categoría exista antes de listar sus productos
            Categoria categoriaExistente = categoriaDAO.buscarPorId(categoriaId);
            if (categoriaExistente == null) {
                throw new ServiceException("La categoría con ID " + categoriaId + " no existe o está eliminada.");
            }
            return productoDAO.listarPorCategoria(categoriaId);
        } catch (SQLException e) {
            throw new ServiceException("Error al listar productos por categoría desde la base de datos: " + e.getMessage(), e);
        }
    }
}
