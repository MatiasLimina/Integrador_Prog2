package com.integrador.foodstore.dao;

import com.integrador.foodstore.domain.DetallePedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DetallePedidoDAO {
    void guardar(DetallePedido d, Long pedidoId, Connection conn) throws SQLException;   // Inserta detalle asociado a un pedido
    List<DetallePedido> listarPorPedido(Long pedidoId) throws SQLException; // Lista detalles de un pedido
    void eliminarPorProducto(Long pedidoId, Long productoId) throws SQLException; // Baja lógica de un detalle
}