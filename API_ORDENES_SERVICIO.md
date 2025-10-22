# API de Órdenes de Servicio - AutoFixPro

## Descripción
Este documento describe el uso de la API REST para gestionar órdenes de servicio en AutoFixPro. Se ha implementado el controlador `OrdenServicioController` con todos los endpoints necesarios para el ciclo de vida completo de una orden de servicio.

## URL Base
```
http://localhost:8080/api/ordenes
```

## Endpoints Disponibles

### 1. Crear una Nueva Orden de Servicio (CU03)

**Endpoint:** `POST /api/ordenes`

**Descripción:** Crea una nueva orden de servicio para un vehículo. Automáticamente se establece el estado inicial como RECIBIDO y se envía una notificación.

**Request Body:**
```json
{
  "vehiculoId": 1,
  "descripcionProblema": "El motor hace un ruido extraño al acelerar",
  "prioridad": "ALTA",
  "costoEstimado": 350.50,
  "tecnicoId": 2
}
```

**Campos:**
- `vehiculoId` (obligatorio): ID del vehículo que ingresa al taller
- `descripcionProblema` (opcional): Descripción del problema reportado
- `prioridad` (opcional): BAJA, NORMAL, ALTA, URGENTE (por defecto: NORMAL)
- `costoEstimado` (opcional): Costo estimado de la reparación
- `tecnicoId` (opcional): ID del técnico asignado

**Response Exitoso (201 Created):**
```json
{
  "success": true,
  "message": "Orden de servicio creada exitosamente",
  "data": {
    "ordenId": 5,
    "fechaIngreso": "2025-10-19T10:30:00",
    "fechaEntrega": null,
    "descripcionProblema": "El motor hace un ruido extraño al acelerar",
    "costoEstimado": 350.50,
    "estadoOrden": "RECIBIDO",
    "prioridad": "ALTA",
    "vehiculo": {
      "vehiculoId": 1,
      "placa": "ABC123",
      "marca": "Toyota",
      "modelo": "Corolla"
    },
    "tecnico": {
      "tecnicoId": 2,
      "nombres": "Juan",
      "apellidos": "Pérez",
      "especialidad": "Mecánica General"
    }
  }
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/api/ordenes \
  -H "Content-Type: application/json" \
  -d '{
    "vehiculoId": 1,
    "descripcionProblema": "El motor hace un ruido extraño al acelerar",
    "prioridad": "ALTA",
    "costoEstimado": 350.50
  }'
```

---

### 2. Listar Todas las Órdenes de Servicio

**Endpoint:** `GET /api/ordenes`

**Descripción:** Obtiene todas las órdenes de servicio registradas.

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Órdenes de servicio obtenidas exitosamente",
  "data": [
    {
      "ordenId": 1,
      "fechaIngreso": "2025-10-15T09:00:00",
      "estadoOrden": "EN_REPARACION",
      "prioridad": "NORMAL",
      "descripcionProblema": "Cambio de aceite y filtros"
    },
    {
      "ordenId": 2,
      "fechaIngreso": "2025-10-16T14:30:00",
      "estadoOrden": "COMPLETADO",
      "prioridad": "ALTA",
      "descripcionProblema": "Reparación de frenos"
    }
  ]
}
```

---

### 3. Obtener una Orden por ID

**Endpoint:** `GET /api/ordenes/{id}`

**Descripción:** Obtiene los detalles de una orden de servicio específica.

**Ejemplo:** `GET /api/ordenes/5`

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Orden de servicio encontrada",
  "data": {
    "ordenId": 5,
    "fechaIngreso": "2025-10-19T10:30:00",
    "fechaEntrega": null,
    "descripcionProblema": "El motor hace un ruido extraño al acelerar",
    "costoEstimado": 350.50,
    "estadoOrden": "RECIBIDO",
    "prioridad": "ALTA"
  }
}
```

---

### 4. Actualizar Progreso de una Orden (CU02)

**Endpoint:** `PUT /api/ordenes/{id}/progreso`

**Descripción:** Actualiza el estado de progreso de una orden de servicio. Envía automáticamente notificaciones al cliente y actualiza el historial de estados del vehículo.

**Request Body:**
```json
{
  "nuevoEstado": "EN_REPARACION",
  "observaciones": "Se detectó falla en la correa de distribución, se procede a reemplazar"
}
```

**Estados Válidos:**
- RECIBIDO
- EN_DIAGNOSTICO
- EN_REPARACION
- EN_PRUEBAS
- COMPLETADO
- ENTREGADO
- CANCELADO

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Progreso actualizado exitosamente",
  "data": {
    "ordenId": 5,
    "estadoOrden": "EN_REPARACION",
    "fechaIngreso": "2025-10-19T10:30:00"
  }
}
```

**Ejemplo con cURL:**
```bash
curl -X PUT http://localhost:8080/api/ordenes/5/progreso \
  -H "Content-Type: application/json" \
  -d '{
    "nuevoEstado": "EN_REPARACION",
    "observaciones": "Iniciando reparación del motor"
  }'
```

---

### 5. Obtener Órdenes por Estado

**Endpoint:** `GET /api/ordenes/estado/{estado}`

**Descripción:** Filtra las órdenes por su estado actual.

**Ejemplo:** `GET /api/ordenes/estado/EN_REPARACION`

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Órdenes obtenidas exitosamente",
  "data": [
    {
      "ordenId": 3,
      "estadoOrden": "EN_REPARACION",
      "descripcionProblema": "Cambio de transmisión"
    },
    {
      "ordenId": 5,
      "estadoOrden": "EN_REPARACION",
      "descripcionProblema": "El motor hace un ruido extraño"
    }
  ]
}
```

---

### 6. Obtener Órdenes de un Técnico

**Endpoint:** `GET /api/ordenes/tecnico/{tecnicoId}`

**Descripción:** Obtiene todas las órdenes asignadas a un técnico específico.

**Ejemplo:** `GET /api/ordenes/tecnico/2`

---

### 7. Obtener Órdenes de un Vehículo

**Endpoint:** `GET /api/ordenes/vehiculo/{vehiculoId}`

**Descripción:** Obtiene el historial de órdenes de un vehículo específico.

**Ejemplo:** `GET /api/ordenes/vehiculo/1`

---

### 8. Asignar Técnico a una Orden

**Endpoint:** `PUT /api/ordenes/{id}/asignar-tecnico`

**Descripción:** Asigna un técnico a una orden de servicio existente.

**Request Body:**
```json
{
  "tecnicoId": 3
}
```

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Técnico asignado exitosamente",
  "data": {
    "ordenId": 5,
    "tecnico": {
      "tecnicoId": 3,
      "nombres": "María",
      "apellidos": "González",
      "especialidad": "Electricidad Automotriz"
    }
  }
}
```

---

### 9. Actualizar Costo Estimado

**Endpoint:** `PUT /api/ordenes/{id}/costo`

**Descripción:** Actualiza el costo estimado de una orden de servicio.

**Request Body:**
```json
{
  "costoEstimado": 450.75
}
```

---

### 10. Completar una Orden

**Endpoint:** `PUT /api/ordenes/{id}/completar`

**Descripción:** Marca una orden como completada, estableciendo automáticamente la fecha de entrega.

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Orden completada exitosamente",
  "data": {
    "ordenId": 5,
    "estadoOrden": "COMPLETADO",
    "fechaIngreso": "2025-10-19T10:30:00",
    "fechaEntrega": "2025-10-19T16:45:00"
  }
}
```

---

### 11. Obtener Órdenes por Período (CU06 - Reportes)

**Endpoint:** `GET /api/ordenes/periodo?fechaInicio={inicio}&fechaFin={fin}`

**Descripción:** Obtiene las órdenes de servicio en un período específico. Útil para generar reportes.

**Parámetros:**
- `fechaInicio`: Fecha de inicio en formato ISO (yyyy-MM-dd'T'HH:mm:ss)
- `fechaFin`: Fecha de fin en formato ISO (yyyy-MM-dd'T'HH:mm:ss)

**Ejemplo:**
```
GET /api/ordenes/periodo?fechaInicio=2025-10-01T00:00:00&fechaFin=2025-10-31T23:59:59
```

**Response Exitoso (200 OK):**
```json
{
  "success": true,
  "message": "Órdenes del período obtenidas exitosamente",
  "data": [
    {
      "ordenId": 1,
      "fechaIngreso": "2025-10-15T09:00:00",
      "estadoOrden": "COMPLETADO"
    },
    {
      "ordenId": 2,
      "fechaIngreso": "2025-10-16T14:30:00",
      "estadoOrden": "EN_REPARACION"
    }
  ]
}
```

---

## Códigos de Respuesta HTTP

- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado exitosamente
- **400 Bad Request**: Error en los datos enviados
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

## Valores Válidos

### Estados de Orden (EstadoOrden)
- RECIBIDO
- EN_DIAGNOSTICO
- EN_REPARACION
- EN_PRUEBAS
- COMPLETADO
- ENTREGADO
- CANCELADO

### Prioridades (Prioridad)
- BAJA
- NORMAL
- ALTA
- URGENTE

## Flujo Típico de Uso

1. **Crear Orden**: Un cliente lleva su vehículo al taller
   ```
   POST /api/ordenes
   ```

2. **Asignar Técnico**: Se asigna un técnico disponible
   ```
   PUT /api/ordenes/{id}/asignar-tecnico
   ```

3. **Actualizar Progreso**: El técnico actualiza el estado según avanza
   ```
   PUT /api/ordenes/{id}/progreso
   Estados: RECIBIDO → EN_DIAGNOSTICO → EN_REPARACION → EN_PRUEBAS → COMPLETADO
   ```

4. **Completar Orden**: Cuando termina la reparación
   ```
   PUT /api/ordenes/{id}/completar
   ```

5. **Entregar Vehículo**: Marcar como entregado
   ```
   PUT /api/ordenes/{id}/progreso
   Estado: ENTREGADO
   ```

## Características Adicionales

- **Notificaciones Automáticas**: Al crear y actualizar órdenes, se envían notificaciones automáticamente al cliente
- **Historial de Estados**: Cada cambio de estado se registra en el historial del vehículo
- **Validaciones**: Se validan todos los datos de entrada antes de procesarlos
- **Respuestas Consistentes**: Todas las respuestas siguen el mismo formato con success, message y data

## Notas Importantes

- Todas las fechas se manejan en formato ISO 8601
- El sistema automáticamente establece la fecha de ingreso al crear una orden
- El estado inicial siempre es RECIBIDO
- La prioridad por defecto es NORMAL si no se especifica
- Al completar una orden, se establece automáticamente la fecha de entrega

## Servicios Relacionados

Este controlador se integra con:
- **OrdenServicioService**: Lógica de negocio
- **VehiculoService**: Gestión de vehículos
- **TecnicoService**: Gestión de técnicos
- **NotificacionServicio**: Sistema de notificaciones
- **EstadoVehiculoManager**: Gestión del historial de estados