package com.integrador.foodstore.dao;

import com.integrador.foodstore.domain.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {
    void guardar(Pedido p) throws SQLException;        // Inserta pedido + detalles (transacción)
    List<Pedido> listar() throws SQLException;         // Lista pedidos activos (eliminado=false)
    Pedido buscarPorId(Long id) throws SQLException;   // Busca pedido por ID
    void actualizar(Pedido p) throws SQLException;     // Cambia estado, formaPago, etc.
    void eliminar(Long id) throws SQLException;        // Baja lógica (eliminado=true)
}
