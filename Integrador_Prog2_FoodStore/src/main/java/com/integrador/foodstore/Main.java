package com.integrador.foodstore;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.domain.Categoria;
import com.integrador.foodstore.domain.Pedido;
import com.integrador.foodstore.domain.Producto;
import com.integrador.foodstore.domain.Usuario;
import com.integrador.foodstore.enums.Estado;
import com.integrador.foodstore.enums.FormaPago;
import com.integrador.foodstore.enums.Rol;
import com.integrador.foodstore.exception.CamposVaciosException;
import com.integrador.foodstore.exception.EmailDuplicadoException;
import com.integrador.foodstore.exception.ServiceException;
import com.integrador.foodstore.exception.UsuarioNoEncontradoException;
import com.integrador.foodstore.service.CategoriaService;
import com.integrador.foodstore.service.PedidoService;
import com.integrador.foodstore.service.ProductoService;
import com.integrador.foodstore.service.UsuarioService;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final PedidoService pedidoService = new PedidoService();
    private static final CategoriaService categoriaService = new CategoriaService();
    private static final ProductoService productoService = new ProductoService();

    public static void main(String[] args) {
        System.out.println("=== Probando conexión a la Base de Datos ===");
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con != null && !con.isClosed()) {
                System.out.println("¡Conexión exitosa a MySQL usando HikariCP!");
                System.out.println("Esquema actual: " + con.getCatalog());
            }
        } catch (Exception e) {
            System.err.println("ERROR al conectar a la base de datos:");
            e.printStackTrace();
        }

        int opcion;
        do {
            System.out.println("\n=== SISTEMA DE PEDIDOS (FOOD STORE) ===");
            System.out.println("1. Categorías");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        menuCategorias();
                        break;
                    case 2:
                        menuProductos();
                        break;
                    case 3:
                        menuUsuarios();
                        break;
                    case 4:
                        menuPedidos();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema... ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción fuera de rango. Reintente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private static void menuCategorias() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE CATEGORÍAS ---");
            System.out.println("1. Listar Categorías");
            System.out.println("2. Crear Categoría");
            System.out.println("3. Editar Categoría");
            System.out.println("4. Eliminar Categoría");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        ejecutarListarCategorias();
                        break;
                    case 2:
                        ejecutarCrearCategoria();
                        break;
                    case 3:
                        ejecutarEditarCategoria();
                        break;
                    case 4:
                        ejecutarEliminarCategoria();
                        break;
                    case 0:
                        System.out.println("Regresando...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese una opción numérica.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private static void ejecutarListarCategorias() {
        try {
            List<Categoria> lista = categoriaService.listarCategorias();
            if (lista.isEmpty()) {
                System.out.println("No hay categorías cargadas en el sistema.");
            } else {
                System.out.println("\nListado de Categorías:");
                for (Categoria c : lista) {
                    System.out.println(c);
                }
            }
        } catch (ServiceException e) {
            System.out.println("❌ Error al listar categorías: " + e.getMessage());
        }
    }

    private static void ejecutarCrearCategoria() {
        try {
            System.out.println("\n=== Crear Nueva Categoría ===");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine();

            Categoria nueva = new Categoria(nombre, descripcion);
            categoriaService.guardarCategoria(nueva);
            System.out.println("✅ Categoría '" + nueva.getNombre() + "' registrada con éxito.");
        } catch (ServiceException e) {
            System.out.println("❌ Error al crear categoría: " + e.getMessage());
        }
    }

    private static void ejecutarEditarCategoria() {
        try {
            System.out.print("\nIngrese el ID de la categoría a modificar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Categoria existente = categoriaService.buscarCategoriaPorId(id);
            if (existente == null) {
                System.out.println("❌ La categoría con ID " + id + " no existe o está eliminada.");
                return;
            }

            System.out.println("Categoría actual: " + existente);
            System.out.print("Nuevo Nombre (" + existente.getNombre() + "): ");
            String nombre = scanner.nextLine();
            if (!nombre.trim().isEmpty()) existente.setNombre(nombre);

            System.out.print("Nueva Descripción (" + existente.getDescripcion() + "): ");
            String descripcion = scanner.nextLine();
            if (!descripcion.trim().isEmpty()) existente.setDescripcion(descripcion);

            categoriaService.actualizarCategoria(existente);
            System.out.println("✅ Categoría actualizada correctamente!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese un ID numérico válido.");
        } catch (ServiceException e) {
            System.out.println("❌ Error al editar categoría: " + e.getMessage());
        }
    }

    private static void ejecutarEliminarCategoria() {
        try {
            System.out.print("\nIngrese el ID de la categoría a eliminar (Baja Lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("¿Está seguro de que desea eliminar lógicamente esta categoría? (S/N): ");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("S")) {
                categoriaService.eliminarCategoria(id);
                System.out.println("✅ Categoría eliminada lógicamente del sistema!");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese un ID numérico válido.");
        } catch (ServiceException e) {
            System.out.println("❌ Error al eliminar categoría: " + e.getMessage());
        }
    }

    private static void menuProductos() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE PRODUCTOS ---");
            System.out.println("1. Listar Productos");
            System.out.println("2. Crear Producto");
            System.out.println("3. Editar Producto");
            System.out.println("4. Eliminar Producto");
            System.out.println("5. Listar Productos por Categoría");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        ejecutarListarProductos();
                        break;
                    case 2:
                        ejecutarCrearProducto();
                        break;
                    case 3:
                        ejecutarEditarProducto();
                        break;
                    case 4:
                        ejecutarEliminarProducto();
                        break;
                    case 5:
                        ejecutarListarProductosPorCategoria();
                        break;
                    case 0:
                        System.out.println("Regresando...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese una opción numérica.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private static void ejecutarListarProductos() {
        try {
            List<Producto> lista = productoService.listarProductos();
            if (lista.isEmpty()) {
                System.out.println("No hay productos cargados en el sistema.");
            } else {
                System.out.println("\nListado de Productos:");
                for (Producto p : lista) {
                    System.out.println(p);
                }
            }
        } catch (ServiceException e) {
            System.out.println("❌ Error al listar productos: " + e.getMessage());
        }
    }

    private static void ejecutarCrearProducto() {
        try {
            System.out.println("\n=== Crear Nuevo Producto ===");

            List<Categoria> categorias = categoriaService.listarCategorias();
            if (categorias.isEmpty()) {
                System.out.println("❌ No hay categorías disponibles. Cree una categoría primero.");
                return;
            }
            System.out.println("\nCategorías disponibles:");
            for (Categoria c : categorias) {
                System.out.println("ID: " + c.getId() + ", Nombre: " + c.getNombre());
            }

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Precio: ");
            Double precio = Double.parseDouble(scanner.nextLine());
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine();
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine());
            System.out.print("Imagen (URL o nombre de archivo): ");
            String imagen = scanner.nextLine();
            System.out.print("¿Está disponible? (true/false): ");
            Boolean disponible = Boolean.parseBoolean(scanner.nextLine());
            System.out.print("ID de Categoría: ");
            Long categoriaId = Long.parseLong(scanner.nextLine());

            Categoria categoriaAsociada = categoriaService.buscarCategoriaPorId(categoriaId);
            if (categoriaAsociada == null) {
                System.out.println("❌ Categoría con ID " + categoriaId + " no encontrada.");
                return;
            }

            Producto nuevo = new Producto(nombre, precio, descripcion, stock, imagen, disponible, categoriaAsociada);
            productoService.guardarProducto(nuevo);
            System.out.println("✅ Producto '" + nuevo.getNombre() + "' registrado con éxito.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Error de formato: Ingrese números válidos para Precio, Stock e ID.");
        } catch (IllegalArgumentException | ServiceException e) {
            System.out.println("❌ Error al crear producto: " + e.getMessage());
        }
    }

    private static void ejecutarEditarProducto() {
        try {
            System.out.print("\nIngrese el ID del producto a modificar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Producto existente = productoService.buscarProductoPorId(id);
            if (existente == null) {
                System.out.println("❌ El producto con ID " + id + " no existe.");
                return;
            }

            System.out.println("Producto actual: " + existente);
            System.out.print("Nuevo Nombre (" + existente.getNombre() + "): ");
            String nombre = scanner.nextLine();
            if (!nombre.trim().isEmpty()) existente.setNombre(nombre);

            System.out.print("Nuevo Precio (" + existente.getPrecio() + "): ");
            String precioStr = scanner.nextLine();
            if (!precioStr.trim().isEmpty()) existente.setPrecio(Double.parseDouble(precioStr));

            System.out.print("Nueva Descripción (" + existente.getDescripcion() + "): ");
            String descripcion = scanner.nextLine();
            if (!descripcion.trim().isEmpty()) existente.setDescripcion(descripcion);

            System.out.print("Nuevo Stock (" + existente.getStock() + "): ");
            String stockStr = scanner.nextLine();
            if (!stockStr.trim().isEmpty()) existente.setStock(Integer.parseInt(stockStr));

            System.out.print("Nueva Imagen (" + existente.getImagen() + "): ");
            String imagen = scanner.nextLine();
            if (!imagen.trim().isEmpty()) existente.setImagen(imagen);

            System.out.print("¿Nuevo estado disponible? (" + existente.getDisponible() + ") (true/false): ");
            String disponibleStr = scanner.nextLine();
            if (!disponibleStr.trim().isEmpty()) existente.setDisponible(Boolean.parseBoolean(disponibleStr));

            List<Categoria> categorias = categoriaService.listarCategorias();
            if (!categorias.isEmpty()) {
                System.out.println("\nCategorías disponibles:");
                for (Categoria c : categorias) {
                    System.out.println("ID: " + c.getId() + ", Nombre: " + c.getNombre());
                }
            }
            System.out.print("Nuevo ID de Categoría (" + (existente.getCategoria() != null ? existente.getCategoria().getId() : "N/A") + "): ");
            String categoriaIdStr = scanner.nextLine();
            if (!categoriaIdStr.trim().isEmpty()) {
                Long nuevaCategoriaId = Long.parseLong(categoriaIdStr);
                Categoria nuevaCategoria = categoriaService.buscarCategoriaPorId(nuevaCategoriaId);
                if (nuevaCategoria != null) {
                    existente.setCategoria(nuevaCategoria);
                } else {
                    System.out.println("❌ Categoría no encontrada. Se mantiene la actual.");
                }
            }

            productoService.actualizarProducto(existente);
            System.out.println("✅ Producto actualizado correctamente!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Error de formato: Ingrese números válidos.");
        } catch (IllegalArgumentException | ServiceException e) {
            System.out.println("❌ Error al editar producto: " + e.getMessage());
        }
    }

    private static void ejecutarEliminarProducto() {
        try {
            System.out.print("\nIngrese el ID del producto a eliminar (Baja Lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("¿Está seguro de que desea eliminar este producto? (S/N): ");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("S")) {
                productoService.eliminarProducto(id);
                System.out.println("✅ Producto eliminado lógicamente!");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese un ID numérico válido.");
        } catch (ServiceException e) {
            System.out.println("❌ Error al eliminar producto: " + e.getMessage());
        }
    }

    private static void ejecutarListarProductosPorCategoria() {
        try {
            List<Categoria> categorias = categoriaService.listarCategorias();
            if (categorias.isEmpty()) {
                System.out.println("❌ No hay categorías disponibles.");
                return;
            }
            System.out.println("\nCategorías disponibles:");
            for (Categoria c : categorias) {
                System.out.println("ID: " + c.getId() + ", Nombre: " + c.getNombre());
            }

            System.out.print("Ingrese el ID de la categoría para listar sus productos: ");
            Long categoriaId = Long.parseLong(scanner.nextLine());

            List<Producto> lista = productoService.listarProductosPorCategoria(categoriaId);
            if (lista.isEmpty()) {
                System.out.println("No hay productos en la categoría con ID " + categoriaId + ".");
            } else {
                System.out.println("\nListado de Productos para Categoría ID " + categoriaId + ":");
                for (Producto p : lista) {
                    System.out.println(p);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese un ID de categoría válido.");
        } catch (ServiceException e) {
            System.out.println("❌ Error al listar productos: " + e.getMessage());
        }
    }

    private static void menuUsuarios() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE USUARIOS ---");
            System.out.println("1. Listar Usuarios");
            System.out.println("2. Crear Usuario");
            System.out.println("3. Editar Usuario");
            System.out.println("4. Eliminar Usuario");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        ejecutarListarUsuarios();
                        break;
                    case 2:
                        ejecutarCrearUsuario();
                        break;
                    case 3:
                        ejecutarEditarUsuario();
                        break;
                    case 4:
                        ejecutarEliminarUsuario();
                        break;
                    case 0:
                        System.out.println("Regresando...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese una opción numérica.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private static void ejecutarListarUsuarios() {
        try {
            List<Usuario> lista = usuarioService.listarUsuarios();
            if (lista.isEmpty()) {
                System.out.println("No hay usuarios activos cargados.");
            } else {
                System.out.println("\nListado de Usuarios:");
                for (Usuario u : lista) {
                    System.out.println(u);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    private static void ejecutarCrearUsuario() {
        try {
            System.out.println("\n=== Crear Nuevo Usuario ===");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Celular: ");
            String celular = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            System.out.print("Seleccione Rol (1. ADMIN / 2. USUARIO): ");
            String rolInput = scanner.nextLine();
            Rol rol = (Integer.parseInt(rolInput) == 1) ? Rol.ADMIN : Rol.USUARIO;

            Usuario nuevo = new Usuario(nombre, apellido, email, celular, password, rol);
            usuarioService.registrarUsuario(nuevo);
            System.out.println("¡Usuario registrado con éxito!");
        } catch (CamposVaciosException | EmailDuplicadoException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
        }
    }

    private static void ejecutarEditarUsuario() {
        try {
            System.out.print("\nIngrese el ID del usuario a modificar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Usuario existente = usuarioService.buscarUsuario(id);
            if (existente == null) {
                System.out.println("El usuario no existe o está dado de baja.");
                return;
            }

            System.out.print("Nuevo Nombre (" + existente.getNombre() + "): ");
            String nombre = scanner.nextLine();
            if (!nombre.trim().isEmpty()) existente.setNombre(nombre);

            System.out.print("Nuevo Apellido (" + existente.getApellido() + "): ");
            String apellido = scanner.nextLine();
            if (!apellido.trim().isEmpty()) existente.setApellido(apellido);

            System.out.print("Nuevo Email (" + existente.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.trim().isEmpty()) existente.setEmail(email);

            System.out.print("Nuevo Celular (" + existente.getCelular() + "): ");
            String celular = scanner.nextLine();
            if (!celular.trim().isEmpty()) existente.setCelular(celular);

            System.out.print("Nueva Contraseña: ");
            String password = scanner.nextLine();
            if (!password.trim().isEmpty()) existente.setPassword(password);

            usuarioService.modificarUsuario(existente);
            System.out.println("¡Usuario actualizado correctamente!");
        } catch (UsuarioNoEncontradoException | EmailDuplicadoException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al editar: " + e.getMessage());
        }
    }

    private static void ejecutarEliminarUsuario() {
        try {
            System.out.print("\nIngrese el ID del usuario a eliminar (Baja Lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("¿Está seguro de que desea eliminar este usuario? (S/N): ");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("S")) {
                usuarioService.eliminarUsuario(id);
                System.out.println("¡Usuario eliminado lógicamente!");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    private static void menuPedidos() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE PEDIDOS ---");
            System.out.println("1. Listar Pedidos");
            System.out.println("2. Crear Pedido");
            System.out.println("3. Editar Pedido");
            System.out.println("4. Eliminar Pedido");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        ejecutarListarPedidos();
                        break;
                    case 2:
                        ejecutarCrearPedido();
                        break;
                    case 3:
                        ejecutarEditarPedido();
                        break;
                    case 4:
                        ejecutarEliminarPedido();
                        break;
                    case 0:
                        System.out.println("Regresando...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese una opción numérica.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    private static void ejecutarListarPedidos() {
        try {
            List<Pedido> lista = pedidoService.listarPedidos();
            if (lista.isEmpty()) {
                System.out.println("No hay pedidos cargados en el sistema.");
            } else {
                System.out.println("\nListado de Pedidos:");
                for (Pedido p : lista) {
                    System.out.println(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    private static void ejecutarCrearPedido() {
        try {
            System.out.println("\n=== Crear Nuevo Pedido ===");
            System.out.print("ID Usuario: ");
            Long usuarioId = Long.parseLong(scanner.nextLine());

            Usuario usuario = usuarioService.buscarUsuario(usuarioId);
            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
                return;
            }

            System.out.print("Forma de pago (EFECTIVO/TARJETA/TRANSFERENCIA): ");
            String formaPagoInput = scanner.nextLine();
            FormaPago formaPago = FormaPago.valueOf(formaPagoInput.toUpperCase());

            Pedido nuevo = new Pedido(usuario, Estado.PENDIENTE, formaPago);
            pedidoService.crearPedido(nuevo);

            System.out.println("¡Pedido creado con éxito!");
        } catch (Exception e) {
            System.out.println("Error al crear pedido: " + e.getMessage());
        }
    }

    private static void ejecutarEditarPedido() {
        try {
            System.out.print("\nIngrese el ID del pedido a modificar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Pedido existente = pedidoService.buscarPedidoPorId(id);
            if (existente == null) {
                System.out.println("El pedido no existe o está dado de baja.");
                return;
            }

            System.out.print("Nuevo Estado (" + existente.getEstado() + ") (PENDIENTE/CONFIRMADO/TERMINADO/CANCELADO): ");
            String estadoInput = scanner.nextLine();
            if (!estadoInput.trim().isEmpty()) {
                existente.setEstado(Estado.valueOf(estadoInput.toUpperCase()));
            }

            System.out.print("Nueva Forma de Pago (" + existente.getFormaPago() + ") (EFECTIVO/TARJETA/TRANSFERENCIA): ");
            String formaPagoInput = scanner.nextLine();
            if (!formaPagoInput.trim().isEmpty()) {
                existente.setFormaPago(FormaPago.valueOf(formaPagoInput.toUpperCase()));
            }

            pedidoService.actualizarPedido(existente);
            System.out.println("¡Pedido actualizado correctamente!");
        } catch (Exception e) {
            System.out.println("Error al editar: " + e.getMessage());
        }
    }

    private static void ejecutarEliminarPedido() {
        try {
            System.out.print("\nIngrese el ID del pedido a eliminar (Baja Lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("¿Está seguro de que desea eliminar este pedido? (S/N): ");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("S")) {
                pedidoService.eliminarPedido(id);
                System.out.println("¡Pedido eliminado lógicamente del sistema!");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }
}