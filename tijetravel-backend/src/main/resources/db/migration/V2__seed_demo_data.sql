-- V2__seed_demo_data.sql: Datos de demostración de Tije Travel

-- 1. SUCURSALES (4 ciudades principales de Argentina)
INSERT INTO sucursales (id, codigo, ciudad, direccion, telefono) VALUES
(1, 'SUC-BUE', 'Buenos Aires', 'Av. Corrientes 1234, CABA', '011-4321-0001'),
(2, 'SUC-COR', 'Córdoba', 'Av. Colón 450, Córdoba', '0351-420-0002'),
(3, 'SUC-BAR', 'San Carlos de Bariloche', 'Mitre 250, Bariloche', '0294-442-0003'),
(4, 'SUC-SLA', 'Salta', 'Caseros 620, Salta', '0387-431-0004');

-- 2. HOTELES (15 hoteles reales verificados con precios base $25.000-$55.000 multiplos de $1.000)
INSERT INTO hoteles (id, codigo, nombre, ciudad, direccion, telefono, capacidad_total, precio_base_noche) VALUES
-- Buenos Aires
(1, 'HOT-BUE-01', 'Alvear Palace Hotel', 'Buenos Aires', 'Av. Alvear 1891', '011-4808-2100', 120, 52000.00),
(2, 'HOT-BUE-02', 'Hilton Buenos Aires', 'Buenos Aires', 'Macacha Güemes 351', '011-4891-0000', 150, 48000.00),
(3, 'HOT-BUE-03', 'NH Collection Centro Histórico', 'Buenos Aires', 'Bolívar 120', '011-4122-3000', 90, 35000.00),
(4, 'HOT-BUE-04', 'Hotel Panamericano', 'Buenos Aires', 'Carlos Pellegrini 551', '011-4348-5000', 110, 38000.00),
-- Córdoba
(5, 'HOT-COR-01', 'Holiday Inn Córdoba', 'Córdoba', 'Fray Luis Beltrán y Cardeñosa', '0351-477-9100', 80, 36000.00),
(6, 'HOT-COR-02', 'Amerian Córdoba Park Hotel', 'Córdoba', 'San Juan 165', '0351-526-6600', 75, 32000.00),
(7, 'HOT-COR-03', 'Azur Real Hotel Boutique', 'Córdoba', 'San Jerónimo 243', '0351-424-7133', 40, 42000.00),
(8, 'HOT-COR-04', 'Windsor Hotel & Tower', 'Córdoba', 'Buenos Aires 214', '0351-422-4012', 60, 34000.00),
-- Bariloche
(9, 'HOT-BAR-01', 'Llao Llao Resort & Spa', 'Bariloche', 'Av. Bustillo Km 25', '0294-444-8530', 140, 55000.00),
(10, 'HOT-BAR-02', 'Hotel Panamericano Bariloche', 'Bariloche', 'San Martín 536', '0294-442-5846', 100, 45000.00),
(11, 'HOT-BAR-03', 'Alma del Lago Suites & Spa', 'Bariloche', 'Av. Bustillo Km 1.151', '0294-452-1000', 70, 49000.00),
(12, 'HOT-BAR-04', 'Edelweiss Hotel', 'Bariloche', 'Av. San Martín 202', '0294-444-5500', 85, 39000.00),
-- Salta
(13, 'HOT-SLA-01', 'Alejandro I Hotel', 'Salta', 'Balcarce 252', '0387-400-0000', 90, 41000.00),
(14, 'HOT-SLA-02', 'Sheraton Salta Hotel', 'Salta', 'Coronel Francisco de Cornejo 46', '0387-432-3000', 100, 44000.00),
(15, 'HOT-SLA-03', 'Hotel Salta', 'Salta', 'Buenos Aires 1', '0387-431-0740', 65, 31000.00);

-- 3. VUELOS (30 vuelos con precios base $150.000-$300.000 multiplos de $10.000)
INSERT INTO vuelos (id, numero, fecha_hora, origen, destino, asientos_totales, cupo_turista, cupo_primera, precio_base) VALUES
-- Buenos Aires a Córdoba
(1, 'TJ-101', '2026-10-21 08:00:00', 'Buenos Aires', 'Córdoba', 150, 120, 30, 160000.00),
(2, 'TJ-102', '2026-10-21 16:30:00', 'Buenos Aires', 'Córdoba', 150, 120, 30, 170000.00),
(3, 'TJ-103', '2026-10-22 09:15:00', 'Buenos Aires', 'Córdoba', 150, 120, 30, 150000.00),
(4, 'TJ-104', '2026-10-23 14:00:00', 'Buenos Aires', 'Córdoba', 150, 120, 30, 180000.00),
(5, 'TJ-105', '2026-10-25 10:30:00', 'Buenos Aires', 'Córdoba', 150, 120, 30, 160000.00),
-- Córdoba a Buenos Aires
(6, 'TJ-106', '2026-10-22 18:00:00', 'Córdoba', 'Buenos Aires', 150, 120, 30, 160000.00),
(7, 'TJ-107', '2026-10-24 11:00:00', 'Córdoba', 'Buenos Aires', 150, 120, 30, 170000.00),
(8, 'TJ-108', '2026-10-26 19:30:00', 'Córdoba', 'Buenos Aires', 150, 120, 30, 150000.00),

-- Buenos Aires a Bariloche
(9, 'TJ-201', '2026-10-21 07:30:00', 'Buenos Aires', 'Bariloche', 180, 140, 40, 240000.00),
(10, 'TJ-202', '2026-10-21 15:00:00', 'Buenos Aires', 'Bariloche', 180, 140, 40, 250000.00),
(11, 'TJ-203', '2026-10-22 08:45:00', 'Buenos Aires', 'Bariloche', 180, 140, 40, 230000.00),
(12, 'TJ-204', '2026-10-23 13:15:00', 'Buenos Aires', 'Bariloche', 180, 140, 40, 260000.00),
(13, 'TJ-205', '2026-10-24 09:00:00', 'Buenos Aires', 'Bariloche', 180, 140, 40, 270000.00),
(14, 'TJ-206', '2026-10-26 12:30:00', 'Buenos Aires', 'Bariloche', 180, 140, 40, 240000.00),
-- Bariloche a Buenos Aires
(15, 'TJ-207', '2026-10-23 17:00:00', 'Bariloche', 'Buenos Aires', 180, 140, 40, 240000.00),
(16, 'TJ-208', '2026-10-25 18:30:00', 'Bariloche', 'Buenos Aires', 180, 140, 40, 250000.00),
(17, 'TJ-209', '2026-10-27 10:00:00', 'Bariloche', 'Buenos Aires', 180, 140, 40, 230000.00),

-- Buenos Aires a Salta
(18, 'TJ-301', '2026-10-21 09:00:00', 'Buenos Aires', 'Salta', 160, 130, 30, 210000.00),
(19, 'TJ-302', '2026-10-22 14:30:00', 'Buenos Aires', 'Salta', 160, 130, 30, 220000.00),
(20, 'TJ-303', '2026-10-23 08:15:00', 'Buenos Aires', 'Salta', 160, 130, 30, 200000.00),
(21, 'TJ-304', '2026-10-25 11:00:00', 'Buenos Aires', 'Salta', 160, 130, 30, 230000.00),
-- Salta a Buenos Aires
(22, 'TJ-305', '2026-10-23 16:00:00', 'Salta', 'Buenos Aires', 160, 130, 30, 210000.00),
(23, 'TJ-306', '2026-10-26 13:45:00', 'Salta', 'Buenos Aires', 160, 130, 30, 220000.00),

-- Córdoba a Bariloche
(24, 'TJ-401', '2026-10-21 11:00:00', 'Córdoba', 'Bariloche', 140, 110, 30, 220000.00),
(25, 'TJ-402', '2026-10-23 10:30:00', 'Córdoba', 'Bariloche', 140, 110, 30, 230000.00),
(26, 'TJ-403', '2026-10-26 15:15:00', 'Córdoba', 'Bariloche', 140, 110, 30, 210000.00),
-- Bariloche a Córdoba
(27, 'TJ-404', '2026-10-24 16:30:00', 'Bariloche', 'Córdoba', 140, 110, 30, 220000.00),

-- Córdoba a Salta
(28, 'TJ-501', '2026-10-22 12:00:00', 'Córdoba', 'Salta', 130, 100, 30, 180000.00),
(29, 'TJ-502', '2026-10-25 08:30:00', 'Córdoba', 'Salta', 130, 100, 30, 190000.00),
-- Salta a Córdoba
(30, 'TJ-503', '2026-10-24 14:00:00', 'Salta', 'Córdoba', 130, 100, 30, 180000.00);

-- 4. TURISTAS (Turistas titulares y familias con DNI único)
INSERT INTO turistas (id, codigo, dni, nombre, apellido, direccion, email, telefono_fijo, telefono_celular, sucursal_id, titular_id) VALUES
-- Familia Perez (Titular: Juan Perez)
(1, 'TUR-001', '35123456', 'Juan', 'Pérez', 'Av. Santa Fe 2345', 'juan.perez@email.com', '011-4777-1111', '011-15-5555-0001', 1, NULL),
(2, 'TUR-002', '36234567', 'María', 'Gómez', 'Av. Santa Fe 2345', 'maria.gomez@email.com', '011-4777-1111', '011-15-5555-0002', 1, 1),
(3, 'TUR-003', '45345678', 'Lucas', 'Pérez Gómez', 'Av. Santa Fe 2345', 'lucas.perez@email.com', NULL, '011-15-5555-0003', 1, 1),

-- Familia Rossi (Titular: Carlos Rossi)
(4, 'TUR-004', '28456789', 'Carlos', 'Rossi', 'Chacabuco 890', 'carlos.rossi@email.com', '0351-460-2222', '0351-15-4444-0004', 2, NULL),
(5, 'TUR-005', '30567890', 'Laura', 'Martínez', 'Chacabuco 890', 'laura.martinez@email.com', '0351-460-2222', '0351-15-4444-0005', 2, 4),

-- Familia Fernandez (Titular: Diego Fernandez)
(6, 'TUR-006', '33678901', 'Diego', 'Fernández', 'San Martín 1200', 'diego.fernandez@email.com', '0294-443-3333', '0294-15-3333-0006', 3, NULL),
(7, 'TUR-007', '34789012', 'Ana', 'López', 'San Martín 1200', 'ana.lopez@email.com', '0294-443-3333', '0294-15-3333-0007', 3, 6),

-- Turistas individuales
(8, 'TUR-008', '29890123', 'Esteban', 'Suárez', 'España 450', 'esteban.suarez@email.com', NULL, '0387-15-2222-0008', 4, NULL),
(9, 'TUR-009', '31901234', 'Patricia', 'Benítez', 'Rivadavia 650', 'patricia.benitez@email.com', '011-4822-4444', '011-15-5555-0009', 1, NULL),
(10, 'TUR-010', '27012345', 'Roberto', 'Álvarez', 'Belgrano 110', 'roberto.alvarez@email.com', '0351-421-5555', '0351-15-4444-0010', 2, NULL);

-- 5. USUARIOS
-- admin123 -> $2a$10$gD3ETq5FvnVA/8K7C89N/esCf1cA5JDXp9/KUk6iXTPV5u9OiCuLS
-- vendedor123 -> $2a$10$cPRlJq6mCTbiYO/M/I..P.GO7yHDVjqH3gk6di0Mqljbmph.zDKcm
-- cliente123 -> $2a$10$G6iwOOAk5rhsbmR59Kuvt.rRRdcy818XVGnpkscJBSNnY.JSWlPiO
INSERT INTO usuarios (id, codigo, username, password, rol, dni) VALUES
(1, 'USR-001', 'admin', '$2a$10$gD3ETq5FvnVA/8K7C89N/esCf1cA5JDXp9/KUk6iXTPV5u9OiCuLS', 'ADMINISTRADOR', '20111222'),
(2, 'USR-002', 'vendedor', '$2a$10$cPRlJq6mCTbiYO/M/I..P.GO7yHDVjqH3gk6di0Mqljbmph.zDKcm', 'VENDEDOR', '20333444'),
(3, 'USR-003', 'cliente', '$2a$10$G6iwOOAk5rhsbmR59Kuvt.rRRdcy818XVGnpkscJBSNnY.JSWlPiO', 'CLIENTE', '35123456'),
(4, 'USR-004', 'carlos.rossi', '$2a$10$G6iwOOAk5rhsbmR59Kuvt.rRRdcy818XVGnpkscJBSNnY.JSWlPiO', 'CLIENTE', '28456789'),
(5, 'USR-005', 'diego.fernandez', '$2a$10$G6iwOOAk5rhsbmR59Kuvt.rRRdcy818XVGnpkscJBSNnY.JSWlPiO', 'CLIENTE', '33678901');

-- 6. RESERVAS DEMO (Para balance inicial y demostracion)
-- Reserva 1: Pagada
-- Vuelo 1: TJ-101 (2026-10-21) Turista ($160.000)
-- Hotel 5: Holiday Inn Cordoba ($36.000 x 3 noches = $108.000)
-- Total = $268.000
INSERT INTO reservas (id, codigo, turista_id, vuelo_id, hotel_id, sucursal_contratacion, clase_vuelo, tipo_hospedaje, fecha_llegada, fecha_partida, precio_vuelo_capturado, precio_hotel_capturado, precio_total, estado, fecha_pago) VALUES
(1, 'RES-DEMO-001', 4, 1, 5, 'SUC-COR (Córdoba)', 'TURISTA', 'ESTANDAR', '2026-10-21', '2026-10-24', 160000.00, 36000.00, 268000.00, 'PAGADO', '2026-10-19 14:30:00');

-- Reserva 2: Pendiente
-- Vuelo 9: TJ-201 (2026-10-21) Primera clase ($240.000 + 10% = $264.000)
-- Hotel 9: Llao Llao Bariloche ($55.000 + 5% pension completa = $57.750 x 2 noches = $115.500)
-- Total = $379.500
INSERT INTO reservas (id, codigo, turista_id, vuelo_id, hotel_id, sucursal_contratacion, clase_vuelo, tipo_hospedaje, fecha_llegada, fecha_partida, precio_vuelo_capturado, precio_hotel_capturado, precio_total, estado, fecha_pago) VALUES
(2, 'RES-DEMO-002', 6, 9, 9, 'SUC-BAR (San Carlos de Bariloche)', 'PRIMERA', 'COMPLETA', '2026-10-21', '2026-10-23', 264000.00, 57750.00, 379500.00, 'PENDIENTE', NULL);

