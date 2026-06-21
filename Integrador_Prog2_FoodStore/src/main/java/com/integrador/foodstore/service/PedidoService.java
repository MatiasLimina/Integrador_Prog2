package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.PedidoDAO;
import com.integrador.foodstore.dao.impl.PedidoDAOImpl;
import com.integrador.foodstore.domain.Pedido;
import com.integrador.foodstore.exception.ServiceException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    private PedidoDAO pedidoDAO = new PedidoDAOImpl();

    public void crearPedido(Pedido pedido) throws ServiceException {
        if (pedido.getUsuario() == null) {
            throw new ServiceException("No se puede crear un pedido sin un usuario asociado.");
        }
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new ServiceException("No se puede crear un pedido sin detalles.");
        }

        try {
            pedidoDAO.guardar(pedido);
        } catch (SQLException e) {
            throw new ServiceException("Error al guardar el pedido en la base de datos: " + e.getMessage(), e);
        }
    }

    public List<Pedido> listarPedidos() throws ServiceException {
        try {
            return pedidoDAO.listar();
        } catch (SQLException e) {
            throw new ServiceException("Error al listar los pedidos: " + e.getMessage(), e);
        }
    }

    public Pedido buscarPedidoPorId(Long id) throws ServiceException {
        try {
            return pedidoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al buscar el pedido: " + e.getMessage(), e);
        }
    }

    public void actualizarPedido(Pedido pedido) throws ServiceException {
        try {
            pedidoDAO.actualizar(pedido);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }

    public void eliminarPedido(Long id) throws ServiceException {
        try {
            pedidoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceException("Error al eliminar el pedido: " + e.getMessage(), e);
        }
    }
}