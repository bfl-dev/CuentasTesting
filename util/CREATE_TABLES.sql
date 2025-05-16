/* *************************************************************
   Script de creación de base de datos – módulo “Cuentas & Sucursales”
   Tablas: sucursal, cliente, personal
   SGBD   : PostgreSQL 11+
   ************************************************************* */

/* 1. Crear (o recrear) el esquema de trabajo */
use cuentas;


/* 3. Tabla: sucursal */
CREATE TABLE sucursal (
                          id              INT AUTO_INCREMENT KEY,
                          direccion       VARCHAR(120) NOT NULL,
                          correo_contacto VARCHAR(120) NOT NULL UNIQUE,
                          tipo_sucursal   ENUM('CASAMATRIZ','SUCURSAL') NOT NULL,
                          fecha_creacion  DATE NOT NULL DEFAULT (CURRENT_DATE)
);

/* 4. Tabla: cliente */
CREATE TABLE cliente (
                         id              SERIAL PRIMARY KEY,
                         nombre          VARCHAR(120) NOT NULL,
                         email           VARCHAR(120) NOT NULL UNIQUE,
                         contraseña      VARCHAR(120) NOT NULL,
                         rut             VARCHAR(12)  NOT NULL UNIQUE,
                         fecha_creacion  DATE NOT NULL DEFAULT (CURRENT_DATE),
                         es_socio        BIT NOT NULL DEFAULT FALSE
);

/* 5. Tabla: personal */
CREATE TABLE personal (
                          id              INT AUTO_INCREMENT KEY,
                          sucursal_id     INT NOT NULL,
                          rol             ENUM('ADMIN','PERSONAL','ANONIMO') NOT NULL,
                          activado        BOOLEAN NOT NULL DEFAULT TRUE,
                          nombre          VARCHAR(120) NOT NULL,
                          email           VARCHAR(120) NOT NULL UNIQUE,
                          contraseña      VARCHAR(120) NOT NULL,
                          rut             VARCHAR(12)  NOT NULL UNIQUE,
                          fecha_creacion  DATE NOT NULL DEFAULT (CURRENT_DATE),
    /* Relación con sucursal */
                          CONSTRAINT fk_personal_sucursal
                              FOREIGN KEY (sucursal_id)
                                  REFERENCES sucursal(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
);

/* 6. Índices recomendados (busquedas frecuentes) */
CREATE INDEX idx_cliente_email      ON cliente  (email);
CREATE INDEX idx_personal_email     ON personal (email);
CREATE INDEX idx_personal_sucursal  ON personal (sucursal_id);