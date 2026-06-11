package com.integrador.foodstore;

import com.integrador.foodstore.config.DatabaseConnection;

import java.sql.Connection;

public class Main {
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
    }
}