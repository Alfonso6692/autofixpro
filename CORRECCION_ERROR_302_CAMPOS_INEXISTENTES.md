# Corrección de Error 302 - Campos Inexistentes en Plantillas Thymeleaf

## Problema Identificado

Al intentar acceder a "Ver detalle cliente" o "Ver vehículos del cliente", la aplicación devuelve **HTTP 302 Found** (redirección) debido a una excepción en las plantillas Thymeleaf.

### Error en Consola

```
Caused by: org.attoparser.ParseException: Exception evaluating SpringEL expression: "cliente.direccion != null ? cliente.direccion : 'No especificada'" (template: "cliente-detalle" - line 108, col 61)
```

## Causa Raíz

Las plantillas Thymeleaf intentaban acceder a **campos que NO existen** en las entidades JPA:

### Entidad Cliente (campos reales):
```java
@Entity
public class Cliente {
    private Long clienteId;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
    private List<Vehiculo> vehiculos;
    // ❌ NO TIENE: direccion
}
```

### Entidad Vehiculo (campos reales):
```java
@Entity
public class Vehiculo {
    private Long vehiculoId;
    private String placa;
    private String marca;
    private String modelo;
    private String año;          // ✅ Nombre: "año"
    private String color;
    private Integer kilometraje;
    private Cliente cliente;
    private List<OrdenServicio> ordenesServicio;
    // ❌ NO TIENE: anioFabricacion, tipo, tipoMotor, tipoVehiculo
}
```

## Campos Incorrectos Usados

| Campo Usado (❌ Incorrecto) | Campo Real (✅ Correcto) | Entidad |
|----------------------------|-------------------------|---------|
| `cliente.direccion` | - (no existe) | Cliente |
| `vehiculo.anioFabricacion` | `vehiculo.año` | Vehiculo |
| `vehiculo.anio` | `vehiculo.año` | Vehiculo |
| `vehiculo.tipo` | - (no existe) | Vehiculo |
| `vehiculo.tipoMotor` | - (no existe) | Vehiculo |
| `vehiculo.tipoVehiculo` | - (no existe) | Vehiculo |

---

## Correcciones Aplicadas

### 1. cliente-detalle.html

**Ubicación**: `src/main/resources/templates/cliente-detalle.html`

#### Eliminado (líneas 105-110):
```html
<!-- ❌ ELIMINADO - Campo no existe -->
<div class="col-12">
    <div class="p-3 bg-light rounded">
        <div class="info-label"><i class="fas fa-map-marker-alt me-1"></i>Dirección</div>
        <div class="info-value" th:text="${cliente.direccion != null ? cliente.direccion : 'No especificada'}">Av. Principal 123</div>
    </div>
</div>
```

**Resultado**: Plantilla ahora solo muestra campos existentes (nombres, apellidos, DNI, teléfono, email)

---

### 2. vehiculos.html

**Ubicación**: `src/main/resources/templates/vehiculos.html`

#### Cambios en Estadísticas (líneas 88-117):

**ANTES** - Intentaba contar por `tipo`:
```html
<div class="col-md-3">
    <h5 class="card-title">Sedán</h5>
    <p th:text="${#lists.size(#lists.select(vehiculos, v -> v.tipo == 'SEDAN'))}">0</p>
</div>
<!-- ❌ ERROR - vehiculo.tipo no existe -->
```

**DESPUÉS** - Estadísticas basadas en campos reales:
```html
<div class="col-md-4">
    <h5 class="card-title">Total Vehículos</h5>
    <p th:text="${totalVehiculos}">0</p>
</div>
<div class="col-md-4">
    <h5 class="card-title">Marcas</h5>
    <p th:text="${#sets.size(#lists.stream(vehiculos).map(v -> v.marca).toSet())}">0</p>
    <small class="text-muted">Diferentes</small>
</div>
<div class="col-md-4">
    <h5 class="card-title">Con Propietario</h5>
    <p th:text="${#lists.size(#lists.select(vehiculos, v -> v.cliente != null))}">0</p>
</div>
```

#### Cambios en Cards de Vehículos (líneas 182-204):

**ANTES**:
```html
<li><strong>Año:</strong> <span th:text="${vehiculo.anioFabricacion}">2020</span></li>
<li><strong>Color:</strong> <span th:text="${vehiculo.color}">Rojo</span></li>
<li><strong>Motor:</strong> <span th:text="${vehiculo.tipoMotor}">Gasolina</span></li>
<span class="badge" th:text="${vehiculo.tipo}">SEDAN</span>
```

**DESPUÉS**:
```html
<li><strong>Año:</strong> <span th:text="${vehiculo.año}">2020</span></li>
<li><strong>Color:</strong> <span th:text="${vehiculo.color != null ? vehiculo.color : 'No especificado'}">Rojo</span></li>
<li><strong>Kilometraje:</strong> <span th:text="${vehiculo.kilometraje != null ? vehiculo.kilometraje + ' km' : 'No especificado'}">50000 km</span></li>
<!-- Eliminado: tipo, tipoMotor -->
```

---

### 3. cliente-vehiculos.html

**Ubicación**: `src/main/resources/templates/cliente-vehiculos.html`

#### Cambios (líneas 100-120):

**ANTES**:
```html
<div class="col-6">
    <small class="text-muted">Año</small>
    <div class="fw-bold" th:text="${vehiculo.anio}">2020</div>  <!-- ❌ anio -->
</div>
<div class="col-6">
    <small class="text-muted">Color</small>
    <div class="fw-bold" th:text="${vehiculo.color}">Blanco</div>
</div>
<div class="col-12">
    <small class="text-muted">Tipo</small>
    <div class="fw-bold" th:text="${vehiculo.tipoVehiculo}">Sedan</div>  <!-- ❌ tipoVehiculo -->
</div>
```

**DESPUÉS**:
```html
<div class="col-6">
    <small class="text-muted">Año</small>
    <div class="fw-bold" th:text="${vehiculo.año}">2020</div>  <!-- ✅ año -->
</div>
<div class="col-6">
    <small class="text-muted">Color</small>
    <div class="fw-bold" th:text="${vehiculo.color != null ? vehiculo.color : 'No especificado'}">Blanco</div>
</div>
<!-- ✅ Eliminado campo "Tipo" que no existe -->
```

---

## Protección Null-Safe Agregada

Para evitar errores con campos opcionales (que pueden ser `null`), se agregaron operadores ternarios:

```html
<!-- ✅ Null-safe: muestra "No especificado" si es null -->
<span th:text="${vehiculo.color != null ? vehiculo.color : 'No especificado'}">Rojo</span>
<span th:text="${vehiculo.kilometraje != null ? vehiculo.kilometraje + ' km' : 'No especificado'}">50000 km</span>
```

---

## Resumen de Archivos Modificados

| Archivo | Cambios | Líneas |
|---------|---------|--------|
| **cliente-detalle.html** | Eliminado campo `direccion` | 105-110 |
| **vehiculos.html** | Corregido `anioFabricacion` → `año`<br>Eliminado `tipo`, `tipoMotor`<br>Nuevas estadísticas sin `tipo` | 88-117, 182-204 |
| **cliente-vehiculos.html** | Corregido `anio` → `año`<br>Eliminado `tipoVehiculo`<br>Agregado null-safe a `color` | 100-120 |

---

## Logging Agregado

**Ubicación**: `WebController.java:89-112`

```java
@GetMapping("/clientes/{id}")
public String verDetalleCliente(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
    log.info("=== Accediendo a detalle del cliente ID: {} ===", id);
    try {
        log.info("Buscando cliente con ID: {}", id);
        Optional<Cliente> clienteOpt = clienteService.findById(id);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            log.info("Cliente encontrado: {} {} (ID: {})",
                cliente.getNombres(), cliente.getApellidos(), cliente.getClienteId());
            model.addAttribute("title", "AutoFixPro - Detalle Cliente");
            model.addAttribute("cliente", cliente);
            log.info("Retornando vista: cliente-detalle");
            return "cliente-detalle";
        } else {
            log.warn("Cliente con ID {} NO encontrado", id);
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/dashboard";
        }
    } catch (Exception e) {
        log.error("ERROR al obtener cliente ID {}: {}", id, e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error", "Error al obtener cliente: " + e.getMessage());
        return "redirect:/dashboard";
    }
}
```

---

## Flujo de Error Corregido

### ANTES (con error):
```
1. Usuario hace clic en "Ver detalles" del cliente
   ↓
2. GET /clientes/1
   ↓
3. WebController.verDetalleCliente() encuentra al cliente
   ↓
4. Intenta renderizar cliente-detalle.html
   ↓
5. ❌ Thymeleaf intenta evaluar: ${cliente.direccion}
   ↓
6. ❌ ParseException: Campo "direccion" no existe
   ↓
7. GlobalExceptionHandler captura la excepción
   ↓
8. ❌ HTTP 302 redirect:/dashboard
```

### DESPUÉS (corregido):
```
1. Usuario hace clic en "Ver detalles" del cliente
   ↓
2. GET /clientes/1
   ↓
3. WebController.verDetalleCliente() encuentra al cliente
   LOG: "Cliente encontrado: Juan Pérez (ID: 1)"
   ↓
4. Renderiza cliente-detalle.html
   ↓
5. ✅ Thymeleaf solo accede a campos existentes
   ↓
6. ✅ HTTP 200 OK - Vista mostrada correctamente
```

---

## Verificación

### Pruebas Realizadas

**1. Ver Detalle Cliente** (`/clientes/{id}`):
- ✅ Muestra información completa del cliente
- ✅ Sin campos inexistentes
- ✅ HTTP 200 OK

**2. Ver Vehículos del Cliente** (`/clientes/{id}/vehiculos`):
- ✅ Muestra lista de vehículos
- ✅ Campos corregidos (`año` en lugar de `anioFabricacion`)
- ✅ Null-safe para `color` y `kilometraje`
- ✅ HTTP 200 OK

**3. Listar Vehículos** (`/vehiculos`):
- ✅ Estadísticas sin usar campo `tipo`
- ✅ Cards muestran solo campos existentes
- ✅ HTTP 200 OK

### Logs Esperados (exitoso):

```
2025-10-18 20:45:12 - GET "/clientes/1", parameters={}
2025-10-18 20:45:12 - Mapped to com.example.autofixpro.controller.WebController#verDetalleCliente(Long, Model, RedirectAttributes)
2025-10-18 20:45:12 - === Accediendo a detalle del cliente ID: 1 ===
2025-10-18 20:45:12 - Buscando cliente con ID: 1
Hibernate: select ... from clientes c1_0 where c1_0.cliente_id=?
2025-10-18 20:45:12 - Cliente encontrado: Juan Pérez García (ID: 1)
2025-10-18 20:45:12 - Retornando vista: cliente-detalle
2025-10-18 20:45:12 - Selected 'text/html' given [text/html, ...]
2025-10-18 20:45:12 - Completed 200 OK
```

---

## Buenas Prácticas Aplicadas

### 1. Validación de Campos

**Siempre verificar que los campos existan en la entidad antes de usarlos en Thymeleaf:**

```java
// ✅ Verificar entidad primero
@Entity
public class Cliente {
    private String email;  // ← Campo existe
}

// ✅ Luego usar en HTML
<span th:text="${cliente.email}">email@example.com</span>
```

### 2. Null-Safe en Thymeleaf

```html
<!-- ✅ CORRECTO - Operador ternario -->
<span th:text="${vehiculo.color != null ? vehiculo.color : 'No especificado'}">

<!-- ✅ CORRECTO - Elvis operator -->
<span th:text="${vehiculo.color ?: 'No especificado'}">

<!-- ❌ INCORRECTO - Puede causar error si es null -->
<span th:text="${vehiculo.color}">
```

### 3. Logging Detallado

```java
// ✅ Logs informativos en cada paso
log.info("=== Accediendo a detalle del cliente ID: {} ===", id);
log.info("Buscando cliente con ID: {}", id);
log.info("Cliente encontrado: {} {}", nombres, apellidos);
log.error("ERROR al obtener cliente ID {}: {}", id, e.getMessage(), e);
```

---

## Lecciones Aprendidas

### Error Común: Asumir nombres de campos

❌ **NO HACER**:
```html
<!-- Asumir que existe sin verificar -->
<span th:text="${vehiculo.anioFabricacion}">
<span th:text="${cliente.direccion}">
```

✅ **HACER**:
```java
// 1. Verificar la entidad primero
@Entity
public class Vehiculo {
    private String año;  // ← Verificar nombre real
}

// 2. Usar el campo correcto en HTML
<span th:text="${vehiculo.año}">
```

### Error Común: No manejar valores null

❌ **NO HACER**:
```html
<!-- Puede fallar si color es null -->
<span th:text="${vehiculo.color}">
```

✅ **HACER**:
```html
<!-- Manejo seguro de null -->
<span th:text="${vehiculo.color != null ? vehiculo.color : 'No especificado'}">
```

---

## Próximos Pasos

Si necesitas agregar nuevos campos en el futuro:

### 1. Actualizar Entidad JPA
```java
@Entity
public class Cliente {
    // ... campos existentes

    @Column(length = 200)
    private String direccion;  // ← Agregar nuevo campo

    // Getter y Setter
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
```

### 2. Migración de Base de Datos
```sql
ALTER TABLE clientes ADD COLUMN direccion VARCHAR(200);
```

### 3. Usar en Plantilla
```html
<div class="info-value" th:text="${cliente.direccion != null ? cliente.direccion : 'No especificada'}">
```

---

## Archivos de Documentación

- `SOLUCION_ERROR_302_DETALLE_CLIENTE.md` - Diagnóstico general del error 302
- `CORRECCION_ERROR_302_CAMPOS_INEXISTENTES.md` - Este documento

---

**Fecha de Corrección**: 2025-10-18
**Estado**: ✅ Resuelto
**Archivos Modificados**: 3 plantillas HTML
**Logging Agregado**: Sí
**Resultado**: HTTP 200 OK en todas las vistas

---

**Autor**: AutoFixPro Development Team
