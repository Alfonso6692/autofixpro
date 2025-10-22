# Solución: Error 302 (Redirect) al Ver Detalle de Cliente

## Problema Reportado

Al intentar acceder al detalle de un cliente desde el dashboard, se obtiene un error **302 Found** (redirección HTTP) en lugar de mostrar la página con la información del cliente.

## Código HTTP 302

**HTTP 302 Found** es un código de redirección que indica que el recurso solicitado ha sido temporalmente movido a otra URL. En este caso, significa que el método `verDetalleCliente()` está devolviendo `redirect:/dashboard` en lugar de la vista `cliente-detalle`.

## Causas Posibles

### 1. Cliente No Encontrado
```java
if (clienteOpt.isPresent()) {
    // ... mostrar detalle
} else {
    redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
    return "redirect:/dashboard";  // ← 302 REDIRECT
}
```

**Síntoma**: El cliente con el ID solicitado no existe en la base de datos.

### 2. Excepción Durante la Búsqueda
```java
try {
    // ...
} catch (Exception e) {
    redirectAttributes.addFlashAttribute("error", "Error al obtener cliente: " + e.getMessage());
    return "redirect:/dashboard";  // ← 302 REDIRECT
}
```

**Síntoma**: Ocurre una excepción al buscar el cliente (por ejemplo, error de conexión a la base de datos).

### 3. Problema con la Vista Thymeleaf
Si la plantilla `cliente-detalle.html` no existe o tiene errores, Thymeleaf podría lanzar una excepción que es capturada por el `GlobalExceptionHandler`, resultando en una redirección.

## Diagnóstico Implementado

### Logging Detallado Agregado

**Ubicación**: `WebController.java:90-112`

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

### Logs Esperados

**Caso Exitoso**:
```
=== Accediendo a detalle del cliente ID: 1 ===
Buscando cliente con ID: 1
Cliente encontrado: Juan Pérez (ID: 1)
Retornando vista: cliente-detalle
```

**Caso Error - Cliente No Encontrado**:
```
=== Accediendo a detalle del cliente ID: 999 ===
Buscando cliente con ID: 999
Cliente con ID 999 NO encontrado
```

**Caso Error - Excepción**:
```
=== Accediendo a detalle del cliente ID: 1 ===
Buscando cliente con ID: 1
ERROR al obtener cliente ID 1: Connection refused
```

## Pasos para Diagnosticar

### 1. Verificar Logs de la Aplicación

Después de intentar acceder al detalle del cliente, busca en los logs las líneas que comienzan con:
```
=== Accediendo a detalle del cliente ID: X ===
```

Esto te dirá exactamente qué está fallando.

### 2. Verificar que el Cliente Existe

Accede a la API REST para confirmar que el cliente existe:
```bash
curl http://localhost:9091/api/clientes/{id}
```

O desde el navegador:
```
http://localhost:9091/api/clientes/1
```

### 3. Verificar Plantilla Thymeleaf

Confirmar que existe la plantilla:
```bash
ls src/main/resources/templates/cliente-detalle.html
```

### 4. Verificar Conexión a Base de Datos

Verificar en los logs al inicio de la aplicación:
```
✅ Base de datos MySQL inicializada correctamente con 9 tablas
🐬 Conectado a MySQL versión: 8.0.42
👥 Clientes registrados: 11
```

## Soluciones Según el Caso

### Caso 1: Cliente No Existe

**Problema**: Intentas acceder a un ID que no existe.

**Solución**: Verifica que uses un ID válido de los clientes mostrados en el dashboard.

**Ejemplo**:
- Dashboard muestra clientes con IDs: 1, 2, 3, 4, 5
- Intentas acceder a `/clientes/999` → Error 302
- Solución: Accede a `/clientes/1` o `/clientes/2`, etc.

### Caso 2: Error de Base de Datos

**Problema**: La base de datos no responde o hay un error de conexión.

**Logs típicos**:
```
ERROR al obtener cliente ID 1: Connection refused
ERROR al obtener cliente ID 1: Communications link failure
```

**Solución**:
1. Verificar que MySQL esté corriendo
2. Verificar credenciales en `application.properties`
3. Verificar conectividad de red

### Caso 3: Error en Plantilla Thymeleaf

**Problema**: La plantilla `cliente-detalle.html` tiene errores de sintaxis.

**Logs típicos**:
```
ERROR al obtener cliente ID 1: Error resolving template [cliente-detalle]
ERROR al obtener cliente ID 1: Template might not exist or might not be accessible
```

**Solución**:
1. Verificar que existe: `src/main/resources/templates/cliente-detalle.html`
2. Verificar sintaxis Thymeleaf
3. Recompilar: `./gradlew clean build`

### Caso 4: Error de Permisos (Security)

**Problema**: Spring Security bloquea el acceso a la ruta.

**Síntoma**: Redirección a `/login` en lugar de al dashboard.

**Solución**: Verificar `SecurityConfig.java` línea 59:
```java
.anyRequest().authenticated()  // ✅ Permite rutas autenticadas
```

## Verificación de Archivos

### 1. WebController.java

**Línea 89-112**: Método `verDetalleCliente()`

✅ Tiene logging detallado
✅ Usa `@PathVariable("id")` con nombre explícito
✅ Maneja excepciones correctamente

### 2. cliente-detalle.html

**Ubicación**: `src/main/resources/templates/cliente-detalle.html`

✅ Existe el archivo
✅ Sintaxis Thymeleaf correcta
✅ Usa variables: `${cliente.nombres}`, `${cliente.apellidos}`, etc.

### 3. SecurityConfig.java

**Línea 59**: `.anyRequest().authenticated()`

✅ Permite acceso a rutas autenticadas (incluyendo `/clientes/{id}`)

## Pruebas Manuales

### Test 1: API REST
```bash
# Verificar que el cliente existe en la API
curl -u admin:admin123 http://localhost:9091/api/clientes/1
```

**Respuesta esperada**:
```json
{
  "success": true,
  "data": {
    "clienteId": 1,
    "nombres": "Juan",
    "apellidos": "Pérez",
    ...
  }
}
```

### Test 2: Vista Web
```
1. Navegar a: http://localhost:9091/login
2. Login: admin / admin123
3. Ir al dashboard: http://localhost:9091/dashboard
4. Clic en "Ver detalles" (ícono ojo) de un cliente
5. Verificar que muestra la página cliente-detalle.html
```

### Test 3: URL Directa
```
http://localhost:9091/clientes/1
```

**Esperado**: Muestra la página de detalle
**Error 302**: Ver logs para diagnóstico

## Cómo Usar el Logging

### 1. Ejecutar la aplicación
```bash
./gradlew bootRun
```

### 2. Acceder al detalle del cliente
Navegar a: `http://localhost:9091/clientes/1`

### 3. Ver los logs en la consola

**Buscar líneas como**:
```
2025-10-18 20:30:45 - === Accediendo a detalle del cliente ID: 1 ===
2025-10-18 20:30:45 - Buscando cliente con ID: 1
2025-10-18 20:30:45 - Cliente encontrado: Juan Pérez (ID: 1)
2025-10-18 20:30:45 - Retornando vista: cliente-detalle
```

### 4. Interpretar el resultado

**Si ves "Cliente encontrado"**: El problema está en la vista Thymeleaf
**Si ves "Cliente NO encontrado"**: El ID no existe en la base de datos
**Si ves "ERROR"**: Hay una excepción - revisar el stack trace

## Ejemplo de Diagnóstico Completo

### Escenario: Cliente con ID 1 existe

**Request**: `GET /clientes/1`

**Logs**:
```
2025-10-18 20:30:45 - GET "/clientes/1", parameters={}
2025-10-18 20:30:45 - Mapped to com.example.autofixpro.controller.WebController#verDetalleCliente(Long, Model, RedirectAttributes)
2025-10-18 20:30:45 - === Accediendo a detalle del cliente ID: 1 ===
2025-10-18 20:30:45 - Buscando cliente con ID: 1
Hibernate:
    select
        c1_0.cliente_id,
        c1_0.nombres,
        c1_0.apellidos,
        c1_0.dni,
        ...
    from
        clientes c1_0
    where
        c1_0.cliente_id=?
2025-10-18 20:30:45 - Cliente encontrado: Juan Pérez García (ID: 1)
2025-10-18 20:30:45 - Retornando vista: cliente-detalle
2025-10-18 20:30:45 - Selected 'text/html' given [text/html, ...]
2025-10-18 20:30:45 - Completed 200 OK
```

**Resultado**: ✅ Exitoso - HTTP 200 OK

### Escenario: Cliente con ID 999 NO existe

**Request**: `GET /clientes/999`

**Logs**:
```
2025-10-18 20:31:10 - GET "/clientes/999", parameters={}
2025-10-18 20:31:10 - Mapped to com.example.autofixpro.controller.WebController#verDetalleCliente(Long, Model, RedirectAttributes)
2025-10-18 20:31:10 - === Accediendo a detalle del cliente ID: 999 ===
2025-10-18 20:31:10 - Buscando cliente con ID: 999
Hibernate:
    select
        c1_0.cliente_id,
        ...
    from
        clientes c1_0
    where
        c1_0.cliente_id=?
2025-10-18 20:31:10 - Cliente con ID 999 NO encontrado
2025-10-18 20:31:10 - View name [redirect:/dashboard], model {}
2025-10-18 20:31:10 - Completed 302 FOUND
2025-10-18 20:31:10 - GET "/dashboard", parameters={}
2025-10-18 20:31:10 - Completed 200 OK
```

**Resultado**: ❌ Error 302 - Cliente no existe

## Próximos Pasos

1. **Intenta acceder** a un detalle de cliente desde el dashboard
2. **Revisa los logs** de la aplicación
3. **Comparte los logs** para análisis específico
4. **Verifica** que el ID del cliente exista en la base de datos

## Archivos Relacionados

- `src/main/groovy/com/example/autofixpro/controller/WebController.java` (línea 89-112)
- `src/main/resources/templates/cliente-detalle.html`
- `src/main/resources/templates/dashboard.html` (botones de acción)
- `src/main/groovy/com/example/autofixpro/config/SecurityConfig.java`

---

**Fecha**: 2025-10-18
**Autor**: AutoFixPro Development Team