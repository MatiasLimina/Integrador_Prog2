package com.integrador.foodstore.service;

import com.integrador.foodstore.dao.PedidoDAO;
import com.integrador.foodstore.dao.impl.PedidoDAOImpl;
import com.integrador.foodstore.domain.Pedido;

import java.sql.SQLException;
import java.util.List;

public class PedidoService {

    private PedidoDAO pedidoDAO = new PedidoDAOImpl();

    // Crear un nuevo pedido con sus detalles
    public void crearPedido(Pedido pedido) {
        try {
            pedidoDAO.guardar(pedido);
            System.out.println("✅ Pedido creado correctamente con ID: " + pedido.getId());
        } catch (SQLException e) {
            System.err.println("❌ Error al crear el pedido: " + e.getMessage());
        }
    }

    // Listar todos los pedidos activos
    public void listarPedidos() {
        try {
            List<Pedido> pedidos = pedidoDAO.listar();
            if (pedidos.isEmpty()) {
                System.out.println("No hay pedidos registrados.");
            } else {
                for (Pedido p : pedidos) {
                    System.out.println(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar pedidos: " + e.getMessage());
        }
    }

    // Buscar un pedido por ID
    public Pedido buscarPedidoPorId(Long id) {
        try {
            Pedido p = pedidoDAO.buscarPorId(id);
            if (p != null) {
                System.out.println("Pedido encontrado: " + p);
            } else {
                System.out.println("No se encontró el pedido con ID: " + id);
            }
            return p;
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar pedido: " + e.getMessage());
            return null;
        }
    }

    // Actualizar un pedido existente
    public void actualizarPedido(Pedido pedido) {
        try {
            pedidoDAO.actualizar(pedido);
            System.out.println("✅ Pedido actualizado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar pedido: " + e.getMessage());
        }
    }

    // Eliminar (baja lógica) un pedido
    public void eliminarPedido(Long id) {
        try {
            pedidoDAO.eliminar(id);
            System.out.println("✅ Pedido eliminado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar pedido: " + e.getMessage());
        }
    }
}