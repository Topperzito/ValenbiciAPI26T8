# ValenbiciAPI26T8

Proyecto Maven Java que conecta con la API de Valenbisi del Ayuntamiento de Valencia
y almacena los datos en una base de datos **MySQL en AWS RDS**.

## Tecnologías
- Java 17 + Maven
- Apache HttpClient 4.5.13
- MySQL Connector/J 9.2.0
- AWS RDS (MySQL 8.0 / MariaDB)
- Swing (JFrame GUI)

## Configuración de AWS RDS
1. Crear instancia RDS desde la consola AWS (MariaDB, capa gratuita).
2. Habilitar acceso público y configurar Security Group (puerto 3306).
3. Anotar el endpoint, usuario y contraseña.
4. Crear la base de datos `valenbicibd` y la tabla `estaciones`:

```sql
CREATE DATABASE valenbicibd;
USE valenbicibd;
CREATE TABLE estaciones (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    address   VARCHAR(200) NOT NULL,
    available INT DEFAULT 0,
    free      INT DEFAULT 0,
    total     INT DEFAULT 0,
    lat       DECIMAL(10,6) DEFAULT 0,
    lon       DECIMAL(10,6) DEFAULT 0,
    updated   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_address (address)
);
```

## Configuración del proyecto
Editar en `ConexionBDD.java` los valores:
- `url` → endpoint de tu instancia RDS
- `user` → admin
- `pass` → tu contraseña

## Ejecución
```bash
mvn compile exec:java
```

## Repositorio
https://github.com/Topperzito/ValenbiciAPI26T8
