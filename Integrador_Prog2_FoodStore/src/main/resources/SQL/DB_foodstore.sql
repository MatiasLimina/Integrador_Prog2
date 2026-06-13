CREATE DATABASE IF NOT EXISTS food_store_tpi;
USE food_store_tpi;

-- Ejemplo de cómo impacta tu clase Base en la tabla Categorias
CREATE TABLE IF NOT EXISTS categorias (
    -- Atributos de la clase Base:
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado TINYINT(1) DEFAULT 0, -- 0 = Activo, 1 = Eliminado (Soft Delete)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Atributos propios de Categoria (según el UML):
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

-- Ejemplo de cómo impacta tu clase Base en la tabla Productos
CREATE TABLE IF NOT EXISTS productos (
    -- Atributos de la clase Base:
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Atributos propios de Producto (según el UML):
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    descripcion VARCHAR(255),
    stock INT NOT NULL,
    imagen VARCHAR(255),
    disponible TINYINT(1) DEFAULT 1,
    categoria_id BIGINT,
    
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS usuarios (
    -- Atributos provenientes de la clase padre 'Base'
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado TINYINT(1) NOT NULL DEFAULT 0, -- 0 = Activo, 1 = Eliminado (Soft Delete)
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Atributos propios de la clase 'Usuario'
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE, -- UNIQUE garantiza la regla de negocio de mail único
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL            -- Almacena el .name() del ENUM (ADMIN, USUARIO)
);

CREATE TABLE IF NOT EXISTS pedidos (
    -- Atributos de la clase padre 'Base'
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Atributos propios de Pedido
    total DOUBLE NOT NULL DEFAULT 0.0,
    estado VARCHAR(30) NOT NULL,     -- Almacena el .name() del ENUM Estado (PENDIENTE, PREPARACION, etc.)
    forma_pago VARCHAR(30) NOT NULL, -- Almacena el .name() del ENUM FormaPago (EFECTIVO, TARJETA, etc.)
    usuario_id BIGINT NOT NULL,      -- Relación N:1 con Usuarios (Quién hizo la compra)
    
    -- Clave foránea que vincula al cliente
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
CREATE TABLE IF NOT EXISTS detalles_pedido (
    -- Atributos de la clase padre 'Base'
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Atributos propios de DetallePedido
    cantidad INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    pedido_id BIGINT NOT NULL,       -- Relación N:1 con el Pedido padre (A qué orden pertenece)
    producto_id BIGINT NOT NULL,     -- Relación N:1 con Productos (Qué comida compró)
    
    -- Claves foráneas para mantener la integridad del negocio
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);
