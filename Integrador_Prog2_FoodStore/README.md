# Food Store - Sistema de Gestión de Pedidos

Este proyecto es un sistema de gestión de pedidos para una tienda de comida, desarrollado en Java. Permite administrar categorías, productos, usuarios y pedidos a través de una interfaz de consola interactiva. La aplicación utiliza JDBC para la persistencia de datos en una base de datos MySQL y sigue una arquitectura por capas para una mejor organización del código.

## Tecnologías Utilizadas

- **Java 21**: Lenguaje de programación principal.
- **Maven**: Gestión de dependencias y construcción del proyecto.
- **MySQL**: Sistema de gestión de bases de datos relacional.
- **JDBC (Java Database Connectivity)**: API para la conexión y ejecución de consultas a la base de datos.
- **HikariCP**: Pool de conexiones JDBC de alto rendimiento.

## Características

- **Gestión de Categorías**: CRUD (Crear, Leer, Actualizar, Eliminar) completo para las categorías de productos.
- **Gestión de Productos**: CRUD completo para los productos, asociándolos a una categoría.
- **Gestión de Usuarios**: CRUD completo para los usuarios del sistema, con roles (ADMIN, USUARIO).
- **Gestión de Pedidos**: Creación, actualización de estado y eliminación lógica de pedidos.
- **Persistencia de Datos**: Todas las operaciones se guardan en una base de datos MySQL.
- **Baja Lógica (Soft Delete)**: Los registros no se eliminan físicamente, sino que se marcan como `eliminado = true` para mantener la integridad del historial.

## Prerrequisitos

- **JDK 21** (o superior).
- **Maven** instalado y configurado en el PATH del sistema.
- **MySQL Server** instalado y en ejecución.

## Configuración de la Base de Datos

1.  Abre tu cliente de MySQL y crea una nueva base de datos.
    ```sql
    CREATE DATABASE food_store_db;
    USE food_store_db;
    ```

2.  Ejecuta el siguiente script SQL para crear todas las tablas necesarias:
    ```sql
    CREATE TABLE categorias (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL UNIQUE,
        descripcion TEXT,
        eliminado BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE productos (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        nombre VARCHAR(150) NOT NULL,
        precio DECIMAL(10, 2) NOT NULL,
        descripcion TEXT,
        stock INT NOT NULL,
        imagen VARCHAR(255),
        disponible BOOLEAN DEFAULT TRUE,
        categoria_id BIGINT,
        eliminado BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (categoria_id) REFERENCES categorias(id)
    );

    CREATE TABLE usuarios (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL,
        apellido VARCHAR(100) NOT NULL,
        email VARCHAR(150) NOT NULL UNIQUE,
        celular VARCHAR(50),
        password VARCHAR(255) NOT NULL,
        rol VARCHAR(50) NOT NULL,
        eliminado BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE pedidos (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        usuario_id BIGINT NOT NULL,
        estado VARCHAR(50) NOT NULL,
        forma_pago VARCHAR(50) NOT NULL,
        total DECIMAL(10, 2) NOT NULL,
        eliminado BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    );

    CREATE TABLE detalles_pedido (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        pedido_id BIGINT NOT NULL,
        producto_id BIGINT NOT NULL,
        cantidad INT NOT NULL,
        subtotal DECIMAL(10, 2) NOT NULL,
        eliminado BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
        FOREIGN KEY (producto_id) REFERENCES productos(id)
    );
    ```

## Configuración del Proyecto

La configuración de la conexión a la base de datos se encuentra en la clase `src/main/java/com/integrador/foodstore/config/DatabaseConnection.java`.

Asegúrate de que los siguientes valores coincidan con tu configuración de MySQL:

- `DB_URL`: La URL de tu base de datos (ej. `jdbc:mysql://localhost:3306/food_store_db`).
- `DB_USER`: Tu nombre de usuario de MySQL.
- `DB_PASSWORD`: Tu contraseña de MySQL.

## Cómo Ejecutar el Proyecto

1.  Clona o descarga este repositorio.
2.  Abre una terminal en la raíz del proyecto.
3.  Compila el proyecto usando Maven:
    ```sh
    mvn clean install
    ```
4.  Ejecuta la aplicación:
    ```sh
    mvn exec:java -Dexec.mainClass="com.integrador.foodstore.Main"
    ```
    La aplicación se iniciará y verás el menú principal en la consola.

## Arquitectura del Proyecto

El código está organizado en los siguientes paquetes para mantener una estructura limpia y escalable:

- `com.integrador.foodstore`: Paquete raíz.
- `├── Main.java`: Punto de entrada de la aplicación y gestión de la interfaz de consola.
- `├── config/`: Contiene la configuración de la conexión a la base de datos (`DatabaseConnection.java`).
- `├── domain/`: Clases que modelan las entidades del negocio (POJOs como `Producto`, `Usuario`, etc.).
- `├── enums/`: Enumeraciones utilizadas en el proyecto (`Rol`, `Estado`, `FormaPago`).
- `├── interfaces/`: Interfaces que definen contratos, como `Calculable`.
- `├── exception/`: Excepciones personalizadas para un mejor manejo de errores.
- `├── dao/`: Interfaces del Patrón de Acceso a Datos (DAO) que definen las operaciones de persistencia.
- `│   └── impl/`: Implementaciones concretas de las interfaces DAO con JDBC.
- `└── service/`: Capa de servicio que contiene la lógica de negocio y coordina las operaciones entre la interfaz y los DAOs.
