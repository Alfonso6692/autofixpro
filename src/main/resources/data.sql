-- Datos de prueba para AutoFixPro
-- ====================================

-- Insertar clientes de prueba
INSERT INTO clientes (nombres, apellidos, dni, telefono, email) VALUES
('Juan Carlos', 'González Pérez', '12345678', '+51912345678', 'juan.gonzalez@email.com'),
('María Elena', 'Rodríguez Silva', '23456789', '+51923456789', 'maria.rodriguez@email.com'),
('Pedro Antonio', 'López Martínez', '34567890', '+51934567890', 'pedro.lopez@email.com'),
('Ana Isabel', 'Fernández Castro', '45678901', '+51945678901', 'ana.fernandez@email.com'),
('Carlos Eduardo', 'Morales Díaz', '56789012', '+51956789012', 'carlos.morales@email.com'),
('Sofía Beatriz', 'Herrera Ruiz', '67890123', '+51967890123', 'sofia.herrera@email.com'),
('Miguel Ángel', 'Vásquez Torres', '78901234', '+51978901234', 'miguel.vasquez@email.com'),
('Laura Patricia', 'Jiménez Flores', '89012345', '+51989012345', 'laura.jimenez@email.com'),
('Roberto Carlos', 'Mendoza Gómez', '90123456', '+51990123456', 'roberto.mendoza@email.com'),
('Carmen Rosa', 'Salinas Vargas', '01234567', '+51901234567', 'carmen.salinas@email.com');

-- Insertar técnicos de prueba
INSERT INTO tecnicos (nombres, apellidos, dni, especialidad, telefono, estado_activo, email) VALUES
('José Luis', 'Ramírez Ortiz', '45452155', 'MECANICA_GENERAL', '+51965409978', true, 'jose_luis@outlook.com'),
('Francisco', 'Gutiérrez Peña', '08988118', 'ELECTRICIDAD', '+51955215356', true, 'peñar2@gmail.com'),
('Antonio', 'Espinoza Rojas', '10444463', 'CARROCERIA', '+51944463210', true, 'espinoza@mail.com'),
('Raúl', 'Contreras Vega', '47736401', 'NEUMATICOS', '+51977364012', true, 'contreras@gmail.com');

-- Insertar vehículos de prueba
INSERT INTO vehiculos (placa, marca, modelo, year, color, kilometraje, cliente_id) VALUES
('ABC123', 'Toyota', 'Corolla', 2020, 'Blanco', 45000, 1),
('DEF456', 'Honda', 'Civic', 2019, 'Azul', 52000, 2),
('GHI789', 'Nissan', 'Sentra', 2021, 'Gris', 28000, 3),
('JKL012', 'Chevrolet', 'Spark', 2018, 'Rojo', 68000, 4),
('MNO345', 'Hyundai', 'Accent', 2022, 'Negro', 15000, 5),
('PQR678', 'Kia', 'Rio', 2020, 'Blanco', 38000, 6),
('STU901', 'Mazda', 'Mazda2', 2019, 'Azul', 48000, 7),
('VWX234', 'Ford', 'Fiesta', 2021, 'Plata', 32000, 8),
('YZA567', 'Volkswagen', 'Polo', 2020, 'Verde', 41000, 9),
('BCD890', 'Suzuki', 'Swift', 2022, 'Amarillo', 12000, 10);

-- Insertar servicios de prueba
INSERT INTO servicios (nombre, descripcion, precio, tiempo_estimado, categoria) VALUES
('Cambio de Aceite', 'Cambio de aceite de motor y filtro', 25000.00, 60, 'MANTENIMIENTO'),
('Alineación y Balanceo', 'Alineación de ruedas y balanceo completo', 35000.00, 120, 'MANTENIMIENTO'),
('Frenos Completos', 'Revisión y cambio de pastillas y discos de freno', 85000.00, 180, 'MECANICA'),
('Mantenimiento General', 'Revisión completa del vehículo', 45000.00, 120, 'MANTENIMIENTO'),
('Cambio de Bujías', 'Cambio de bujías y revisión del sistema de encendido', 32000.00, 60, 'ELECTRICA'),
('Revisión Eléctrica', 'Diagnóstico y reparación del sistema eléctrico', 55000.00, 240, 'ELECTRICA'),
('Cambio de Batería', 'Cambio de batería del vehículo', 75000.00, 60, 'ELECTRICA'),
('Reparación de Motor', 'Reparación general del motor', 250000.00, 480, 'MECANICA');

-- Insertar órdenes de servicio de prueba
INSERT INTO ordenes_servicio (fecha_ingreso, fecha_salida_estimada, descripcion_problema, estado_orden, total, vehiculo_id, tecnico_id) VALUES
('2024-09-20 08:00:00', '2024-09-20 17:00:00', 'Cambio de aceite programado', 'COMPLETADO', 25000.00, 1, 1),
('2024-09-22 09:00:00', '2024-09-22 18:00:00', 'Ruido en frenos delanteros', 'EN_REPARACION', 85000.00, 2, 1),
('2024-09-23 10:00:00', '2024-09-24 12:00:00', 'Falla en sistema eléctrico', 'EN_DIAGNOSTICO', 55000.00, 3, 2),
('2024-09-24 11:00:00', '2024-09-24 19:00:00', 'Mantenimiento preventivo', 'RECIBIDO', 45000.00, 4, 1),
('2024-09-25 08:30:00', '2024-09-25 16:30:00', 'Cambio de bujías', 'EN_PRUEBAS', 32000.00, 5, 1);

-- Insertar estados de vehículos
INSERT INTO estados_vehiculo (fecha_actualizacion, estado, porcentaje_avance, observaciones, orden_servicio_id) VALUES
('2024-09-20 08:00:00', 'RECIBIDO', 10, 'Vehículo ingresado al taller', 1),
('2024-09-20 09:00:00', 'EN_REPARACION', 50, 'Iniciando cambio de aceite', 1),
('2024-09-20 16:00:00', 'COMPLETADO', 100, 'Cambio de aceite completado', 1),
('2024-09-22 09:00:00', 'RECIBIDO', 10, 'Vehículo ingresado para revisión de frenos', 2),
('2024-09-22 10:00:00', 'EN_DIAGNOSTICO', 25, 'Revisando sistema de frenos', 2),
('2024-09-22 14:00:00', 'EN_REPARACION', 50, 'Cambiando pastillas de freno', 2),
('2024-09-23 10:00:00', 'RECIBIDO', 10, 'Vehículo con falla eléctrica', 3),
('2024-09-23 11:00:00', 'EN_DIAGNOSTICO', 25, 'Diagnosticando sistema eléctrico', 3);

-- Insertar notificaciones de prueba
INSERT INTO notifications (tipo_notificacion, mensajes, fecha_creacion, fecha_envio, canal, estado_envio, destinatario) VALUES
('INGRESO', 'Su vehículo ABC123 ha ingresado al taller AutoFixPro', '2024-09-20 08:00:00', '2024-09-20 08:05:00', 'EMAIL', 'ENVIADO', 'juan.gonzalez@email.com'),
('ACTUALIZACION', 'Estado actualizado: EN_REPARACION para vehículo ABC123', '2024-09-20 09:00:00', '2024-09-20 09:05:00', 'SMS', 'ENVIADO', '+51912345678'),
('COMPLETADO', 'Su vehículo ABC123 está listo para recoger', '2024-09-20 16:00:00', '2024-09-20 16:05:00', 'EMAIL', 'ENVIADO', 'juan.gonzalez@email.com'),
('INGRESO', 'Su vehículo DEF456 ha ingresado al taller AutoFixPro', '2024-09-22 09:00:00', '2024-09-22 09:05:00', 'SMS', 'ENVIADO', '+51923456789'),
('ACTUALIZACION', 'Estado actualizado: EN_DIAGNOSTICO para vehículo GHI789', '2024-09-23 11:00:00', '2024-09-23 11:05:00', 'EMAIL', 'ENVIADO', 'pedro.lopez@email.com');