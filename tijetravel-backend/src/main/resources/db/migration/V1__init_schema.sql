-- V1__init_schema.sql: Esquema inicial de Tije Travel

CREATE TABLE IF NOT EXISTS sucursales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    ciudad VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sucursales_ciudad ON sucursales(ciudad);

CREATE TABLE IF NOT EXISTS hoteles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    capacidad_total INT NOT NULL,
    precio_base_noche DECIMAL(12,2) NOT NULL,
    CONSTRAINT uk_hotel_nombre_ciudad UNIQUE (nombre, ciudad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_hoteles_ciudad ON hoteles(ciudad);

CREATE TABLE IF NOT EXISTS vuelos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(30) NOT NULL UNIQUE,
    fecha_hora DATETIME NOT NULL,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    asientos_totales INT NOT NULL,
    cupo_turista INT NOT NULL,
    cupo_primera INT NOT NULL,
    precio_base DECIMAL(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_vuelos_origen_destino_fecha ON vuelos(origen, destino, fecha_hora);

CREATE TABLE IF NOT EXISTS turistas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefono_fijo VARCHAR(50) NULL,
    telefono_celular VARCHAR(50) NOT NULL,
    sucursal_id BIGINT NOT NULL,
    titular_id BIGINT NULL,
    CONSTRAINT fk_turistas_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE RESTRICT,
    CONSTRAINT fk_turistas_titular FOREIGN KEY (titular_id) REFERENCES turistas(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_turistas_dni ON turistas(dni);
CREATE INDEX idx_turistas_titular ON turistas(titular_id);
CREATE INDEX idx_turistas_sucursal ON turistas(sucursal_id);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    dni VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_dni ON usuarios(dni);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);

CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    turista_id BIGINT NOT NULL,
    vuelo_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    sucursal_contratacion VARCHAR(255) NOT NULL,
    clase_vuelo VARCHAR(30) NOT NULL,
    tipo_hospedaje VARCHAR(30) NOT NULL,
    fecha_llegada DATE NOT NULL,
    fecha_partida DATE NOT NULL,
    precio_vuelo_capturado DECIMAL(12,2) NOT NULL,
    precio_hotel_capturado DECIMAL(12,2) NOT NULL,
    precio_total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_pago DATETIME NULL,
    CONSTRAINT fk_reservas_turista FOREIGN KEY (turista_id) REFERENCES turistas(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservas_vuelo FOREIGN KEY (vuelo_id) REFERENCES vuelos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservas_hotel FOREIGN KEY (hotel_id) REFERENCES hoteles(id) ON DELETE RESTRICT,
    CONSTRAINT uk_reserva_turista_vuelo UNIQUE (turista_id, vuelo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_reservas_turista ON reservas(turista_id);
CREATE INDEX idx_reservas_vuelo ON reservas(vuelo_id);
CREATE INDEX idx_reservas_hotel ON reservas(hotel_id);
CREATE INDEX idx_reservas_fechas ON reservas(fecha_llegada, fecha_partida);
CREATE INDEX idx_reservas_estado ON reservas(estado);

