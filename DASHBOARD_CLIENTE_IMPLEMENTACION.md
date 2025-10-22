# 🚗 Dashboard de Cliente - AutoFixPro

## 📋 Resumen de la Implementación

Se ha creado un dashboard personalizado para clientes donde pueden ver en tiempo real el progreso de los servicios de sus vehículos.

---

## ✨ Características Implementadas

### 1. **Dashboard Personalizado** (`/cliente-dashboard`)

Un dashboard exclusivo para clientes con:

- **Información personal del cliente**
- **Lista de todos sus vehículos registrados**
- **Estado en tiempo real de cada servicio**
- **Progreso visual de reparación**
- **Historial de estados**
- **Auto-refresh cada 30 segundos**

### 2. **Información Mostrada por Vehículo**

Para cada vehículo se muestra:

#### Datos del Vehículo
- ✅ Marca y Modelo
- ✅ Año
- ✅ Placa
- ✅ Color
- ✅ Kilometraje

#### Datos de la Orden de Servicio
- ✅ **Número de Orden**
- ✅ **Estado Actual** (con badge de color)
- ✅ **Fecha de Ingreso**
- ✅ **Mecánico Asignado** (nombres y apellidos)
- ✅ **Costo Estimado**
- ✅ **Descripción del Problema**

#### Progreso de Reparación
- ✅ **Barra de progreso visual** que muestra el porcentaje según el estado:
  - RECIBIDO: 10%
  - EN_DIAGNOSTICO: 25%
  - EN_REPARACION: 50%
  - EN_PRUEBAS: 80%
  - COMPLETADO: 100%

#### Timeline de Estados
- ✅ Historial completo de cambios de estado
- ✅ Fecha y hora de cada cambio
- ✅ Observaciones del técnico en cada etapa

---

## 📁 Archivos Creados/Modificados

### Nuevos Archivos

1. **`cliente-dashboard.html`**
   - Ubicación: `src/main/resources/templates/cliente-dashboard.html`
   - Vista principal del dashboard de clientes
   - Diseño responsive y moderno
   - Auto-refresh cada 30 segundos

2. **`ClienteDashboardController.java`**
   - Ubicación: `src/main/groovy/com/example/autofixpro/controller/ClienteDashboardController.java`
   - Controlador que maneja las peticiones del dashboard
   - Inicializa correctamente las relaciones lazy (vehículos, órdenes, técnicos, estados)

### Archivos Modificados

3. **`CustomAuthenticationSuccessHandler.java`**
   - Modificado para redirigir a `/cliente-dashboard` cuando el usuario tiene rol `ROLE_USER`
   - Línea 41: `redirectUrl = "/cliente-dashboard";`

4. **`SecurityConfig.java`**
   - Agregado permiso para rutas de cliente
   - Línea 64: `.requestMatchers("/cliente-dashboard", "/cliente/**").hasRole("USER")`

5. **`login.html`**
   - Agregado token CSRF a los 3 formularios
   - Mejoras visuales en pestañas de roles

---

## 🎨 Diseño Visual

### Colores por Estado
- **RECIBIDO**: Gris (#6c757d)
- **EN_DIAGNOSTICO**: Azul cyan (#17a2b8)
- **EN_REPARACION**: Amarillo (#ffc107)
- **EN_PRUEBAS**: Naranja (#fd7e14)
- **COMPLETADO**: Verde (#28a745)
- **ENTREGADO**: Morado (#6f42c1)

### Características de UI/UX
- ✅ Diseño responsive (funciona en móvil)
- ✅ Colores intuitivos según el estado
- ✅ Iconos FontAwesome para mejor comprensión
- ✅ Animaciones suaves en hover
- ✅ Timeline visual del historial
- ✅ Barra de progreso animada
- ✅ Auto-refresh automático

---

## 🔐 Seguridad

### Control de Acceso
- Solo usuarios con rol `ROLE_USER` pueden acceder
- Los clientes SOLO ven sus propios vehículos
- La asociación se hace por email (Usuario.email = Cliente.email)
- Spring Security maneja automáticamente la autenticación

### Redirección Automática
Después del login exitoso:
- **ROLE_USER** (Cliente) → `/cliente-dashboard`
- **ROLE_ADMIN** → `/dashboard`
- **ROLE_TECNICO** → `/dashboard`
- **ROLE_RECEPCIONISTA** → `/dashboard`

---

## 🚀 Cómo Usar

### Para Clientes

1. **Iniciar Sesión**
   - Ve a: `http://localhost:9091/login`
   - Selecciona la pestaña "Cliente"
   - Ingresa tus credenciales
   - Serás redirigido automáticamente a tu dashboard

2. **Ver tus Vehículos**
   - Verás todos tus vehículos registrados
   - Cada vehículo muestra sus datos completos

3. **Ver Servicios en Progreso**
   - Para cada vehículo con servicio activo verás:
     - Estado actual con color
     - Progreso en %
     - Mecánico asignado
     - Costo estimado
     - Timeline de cambios

4. **Auto-Refresh**
   - La página se actualiza automáticamente cada 30 segundos
   - Verás los cambios de estado en tiempo real

### Para Administradores/Técnicos

Para crear datos de prueba y vincular un usuario con un cliente:

```sql
-- 1. Asegúrate de que el cliente tenga el mismo email que el usuario
UPDATE clientes
SET email = 'cliente1@email.com'
WHERE dni = '12345678';

-- 2. Verifica que el usuario exista
SELECT * FROM usuarios WHERE email = 'cliente1@email.com';

-- 3. Verifica que el cliente tenga vehículos
SELECT v.* FROM vehiculos v
JOIN clientes c ON v.cliente_id = c.cliente_id
WHERE c.email = 'cliente1@email.com';

-- 4. Crea una orden de servicio de prueba
INSERT INTO ordenes_servicio (
    vehiculo_id,
    fecha_ingreso,
    descripcion_problema,
    costo_estimado,
    estado_orden,
    prioridad,
    tecnico_id
)
VALUES (
    1,  -- ID del vehículo
    NOW(),
    'Problema con el motor, hace ruidos extraños al acelerar',
    500.00,
    'EN_REPARACION',
    'NORMAL',
    1   -- ID del técnico
);
```

---

## 📊 Modelo de Datos Utilizado

### Relaciones
```
Usuario (email) ←→ Cliente (email)
    ↓
Cliente ←→ Vehiculo (1:N)
    ↓
Vehiculo ←→ OrdenServicio (1:N)
    ↓
OrdenServicio ←→ Tecnico (N:1)
    ↓
OrdenServicio ←→ EstadoVehiculo (1:N)
```

### Lazy Loading
El controlador inicializa correctamente las siguientes relaciones lazy:
- `cliente.vehiculos`
- `vehiculo.ordenesServicio`
- `orden.tecnico`
- `orden.estadosVehiculo`

---

## 🔄 Auto-Refresh

La página se recarga automáticamente cada 30 segundos para mostrar cambios en tiempo real.

```javascript
// Auto-refresh cada 30 segundos
setInterval(function() {
    location.reload();
}, 30000);
```

Si quieres cambiar el intervalo, modifica el valor `30000` (en milisegundos) en `cliente-dashboard.html` línea 433.

---

## 🎯 Estados de la Orden

| Estado | Descripción | Progreso |
|--------|-------------|----------|
| RECIBIDO | Vehículo recibido en taller | 10% |
| EN_DIAGNOSTICO | Diagnóstico en proceso | 25% |
| EN_REPARACION | Reparación en curso | 50% |
| EN_PRUEBAS | Pruebas de funcionamiento | 80% |
| COMPLETADO | Reparación finalizada | 100% |
| ENTREGADO | Vehículo entregado | 100% |

---

## 🐛 Solución de Problemas

### No veo mis vehículos

**Causa**: El email del Usuario no coincide con el email del Cliente

**Solución**:
```sql
-- Actualiza el email del cliente para que coincida con el usuario
UPDATE clientes
SET email = (SELECT email FROM usuarios WHERE username = 'tu_usuario')
WHERE dni = 'tu_dni';
```

### No veo órdenes de servicio

**Causa**: Tu vehículo no tiene órdenes activas

**Solución**: Solicita un servicio en el taller o crea una orden de prueba con el SQL anterior

### Error 403 (Forbidden)

**Causa**: Tu usuario no tiene el rol `ROLE_USER`

**Solución**:
```sql
UPDATE usuarios
SET role = 'USER'
WHERE username = 'tu_usuario';
```

### El técnico aparece como "Por asignar"

**Causa**: La orden no tiene un técnico asignado (`tecnico_id` es NULL)

**Solución**:
```sql
UPDATE ordenes_servicio
SET tecnico_id = 1  -- ID de un técnico existente
WHERE orden_id = 1;  -- ID de la orden
```

---

## 🔮 Mejoras Futuras (Opcionales)

1. **Notificaciones Push**
   - Notificar al cliente cuando cambia el estado
   - Usar WebSockets para updates en tiempo real

2. **Chat con el Mecánico**
   - Mensajería directa con el técnico asignado

3. **Historial Completo**
   - Ver todas las órdenes pasadas
   - Descargar facturas

4. **Calificación del Servicio**
   - Sistema de rating para el servicio recibido

5. **Galería de Fotos**
   - Ver fotos del progreso de la reparación

---

## 📞 URLs Importantes

- **Login**: `http://localhost:9091/login`
- **Dashboard Cliente**: `http://localhost:9091/cliente-dashboard`
- **Perfil**: `http://localhost:9091/perfil`
- **Logout**: `http://localhost:9091/logout`

---

## ✅ Checklist de Implementación

- [x] Crear vista HTML `cliente-dashboard.html`
- [x] Crear controlador `ClienteDashboardController.java`
- [x] Actualizar `CustomAuthenticationSuccessHandler.java`
- [x] Actualizar `SecurityConfig.java`
- [x] Agregar tokens CSRF a formularios de login
- [x] Corregir método `getNombres()` vs `getNombre()`
- [x] Diseño responsive y moderno
- [x] Colores intuitivos por estado
- [x] Auto-refresh automático
- [x] Timeline de historial de estados
- [x] Barra de progreso visual
- [x] Manejo correcto de lazy loading
- [x] Compilación exitosa
- [x] Documentación completa

---

**Última actualización**: 2025-10-21
**Versión**: 1.0
**Estado**: ✅ Implementación Completa
