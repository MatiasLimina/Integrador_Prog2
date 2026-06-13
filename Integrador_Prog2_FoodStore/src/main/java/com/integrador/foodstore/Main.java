package com.integrador.foodstore;

import com.integrador.foodstore.config.DatabaseConnection;
import com.integrador.foodstore.domain.Usuario;
import com.integrador.foodstore.enums.Rol;
import com.integrador.foodstore.exception.CamposVaciosException;
import com.integrador.foodstore.exception.EmailDuplicadoException;
import com.integrador.foodstore.exception.UsuarioNoEncontradoException;
import com.integrador.foodstore.service.UsuarioService;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UsuarioService usuarioService = new UsuarioService();
    public static void main(String[] args) {
        /*
        ---- PRUEBAS ----
         */
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

        /*
        ---- FIN DE PRUEBAS ----
         */
    // ------------------------------------------------------------------------------------

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
                        // TO DO
                        System.out.println("Módulo de Categorías");
                        break;
                    case 2:
                        // TO DO
                        System.out.println("Módulo de Productos ");
                        break;
                    case 3:
                        menuUsuarios();
                        break;
                    case 4:
                        // TO DO
                        System.out.println("Módulo de Pedidos ");
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
                System.out.println("No hay usuarios activos cargados en el sistema."); // HU-USR-01
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
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            System.out.print("Seleccione Rol (1. ADMIN / 2. USUARIO): ");
            String rolInput = scanner.nextLine();

            Rol rol = null;
            if (!rolInput.trim().isEmpty()) {
                try {
                    int rolOpc = Integer.parseInt(rolInput);
                    rol = (rolOpc == 1) ? Rol.ADMIN : Rol.USUARIO;
                } catch (NumberFormatException e) {
                    // Si ingresó texto en vez de un número, tiramos CamposVaciosException o lo dejamos pasar
                    // para que falle controladamente en las validaciones
                }
            }

            Usuario nuevo = new Usuario(nombre, apellido, email, password, rol);
            usuarioService.registrarUsuario(nuevo);
            System.out.println("¡Usuario registrado con éxito!");
        } catch (CamposVaciosException e) {
            // Captura específica para tu excepción
            System.out.println("❌ " + e.getMessage());
            System.out.println("Por favor, vuelva a intentar completando todos los datos solicitados.");

        } catch (EmailDuplicadoException e) {
            System.out.println("📧 " + e.getMessage());
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
                System.out.println("El usuario no existe o está dado de baja."); // HU-USR-03
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

            System.out.print("Nueva Contraseña: ");
            String password = scanner.nextLine();
            if (!password.trim().isEmpty()) existente.setPassword(password);

            usuarioService.modificarUsuario(existente);
            System.out.println("¡Usuario actualizado correctamente!");
        }catch (UsuarioNoEncontradoException e){
            System.out.println("❌ " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Error al editar: " + e.getMessage());
        }
    }

    private static void ejecutarEliminarUsuario() {
        try {
            System.out.print("\nIngrese el ID del usuario a eliminar (Baja Lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("¿Está seguro de que desea eliminar este usuario? (S/N): "); // HU-USR-04
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("S")) {
                usuarioService.eliminarUsuario(id);
                System.out.println("¡Usuario eliminado lógicamente del catálogo!");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (UsuarioNoEncontradoException e){
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

}
