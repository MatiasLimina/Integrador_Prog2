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
