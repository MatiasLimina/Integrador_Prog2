📋 Requisitos Previos

Antes de configurar y poner en marcha la aplicación, asegúrate de contar con las siguientes herramientas instaladas:

    Java Development Kit (JDK) 21

    Apache Maven 3.9+

    MySQL Server 8.0+

🔧 Configuración e Instalación
1. Preparación de la Base de Datos

Accede a tu gestor de base de datos MySQL (CLI, Workbench, phpMyAdmin, etc.) y 
da de alta tu base de datos.

2. Configuración de Credenciales

Abre el archivo de configuración localizado en:
src/main/java/com/integrador/foodstore/config/DatabaseConnection.java

Modifica las propiedades de la URL de conexión, usuario y contraseña de MySQL para 
que coincidan con los de tu servidor local:

// Ejemplo de configuración típica interna
config.setJdbcUrl("jdbc:mysql://localhost:3606/food_store_tpi");
config.setUsername("tu_usuario");
config.setPassword("tu_contraseña");

📦 Compilación y Ejecución con Maven

Navega desde la terminal a la carpeta raíz del proyecto (donde se sitúa el archivo pom.xml)
y ejecuta los siguientes comandos:

Limpiar y Compilar el Proyecto

Para descargar las dependencias declaradas (incluyendo el driver de MySQL y HikariCP), 
compilar el código fuente y empaquetar la aplicación, ejecuta:
mvn clean install

Ejecutar la Aplicación

Una vez compilado correctamente, puedes iniciar la interfaz de consola ejecutando la clase principal a través de Maven:
mvn exec:java -Dexec.mainClass="com.integrador.foodstore.Main"

O bien, si el empaquetado generó un archivo JAR ejecutable en el directorio target/:
java -jar target/foodstore-1.0-SNAPSHOT.jar

👤 Autores
Limina Matias
Monjelardi Nicolas
Agüero Lautaro

Este proyecto fue desarrollado con fines académicos y de integración profesional 
de conceptos de POO, JDBC y Patrones de Diseño de Software.