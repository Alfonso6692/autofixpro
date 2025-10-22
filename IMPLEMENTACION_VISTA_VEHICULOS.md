# Vista Web de Vehículos - Implementación

## Resumen

Se ha implementado una **vista web completa para gestionar vehículos** en AutoFixPro, similar al sistema de clientes existente. Esta vista permite visualizar todos los vehículos registrados con una interfaz atractiva y funcional.

## Cambios Implementados

### 1. WebController.java

**Ubicación**: `src/main/groovy/com/example/autofixpro/controller/WebController.java`

#### Imports Agregados (línea 5):
```java
import com.example.autofixpro.entity.Vehiculo;
```

#### Nuevo Método (líneas 69-82):
```java
@GetMapping("/vehiculos")
public String listarVehiculos(Model model) {
    try {
        List<Vehiculo> vehiculos = vehiculoService.findAll();
        model.addAttribute("title", "AutoFixPro - Vehículos");
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("totalVehiculos", vehiculos.size());
        return "vehiculos";
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Error al cargar vehículos: " + e.getMessage());
        return "redirect:/dashboard";
    }
}
```

**Funcionalidad**:
- Endpoint: `GET /vehiculos`
- Obtiene todos los vehículos desde `vehiculoService.findAll()`
- Agrega variables al modelo para Thymeleaf:
  - `title`: Título de la página
  - `vehiculos`: Lista de vehículos
  - `totalVehiculos`: Conteo total
- Renderiza la plantilla `vehiculos.html`
- Manejo de excepciones con redirección al dashboard

---

### 2. vehiculos.html

**Ubicación**: `src/main/resources/templates/vehiculos.html`

Nueva plantilla HTML completa con:

#### Características Principales:

**Diseño Responsive**:
- Bootstrap 5 para layout adaptativo
- Grid de 3 columnas en desktop, 2 en tablet, 1 en móvil
- Sidebar con navegación consistente

**Estadísticas Dashboard**:
```html
<div class="row mb-4">
    <div class="col-md-3">Total Vehículos</div>
    <div class="col-md-3">Sedán</div>
    <div class="col-md-3">SUV / Camioneta</div>
    <div class="col-md-3">Furgoneta</div>
</div>
```

Usa expresiones Thymeleaf para contar por tipo:
```html
th:text="${#lists.size(#lists.select(vehiculos, v -> v.tipo == 'SEDAN'))}"
```

**Tarjetas de Vehículos**:
Cada vehículo se muestra en una card con:
- Color dinámico según tipo (SEDAN=azul, SUV=amarillo, etc.)
- Información completa:
  - Placa (en header)
  - Marca y modelo
  - Año de fabricación
  - Color
  - Kilometraje
  - Tipo de motor
  - Propietario (con validación null-safe)

**Acciones Rápidas**:
- Registrar Vehículo
- Buscar por Placa
- Filtrar por Tipo
- Exportar Lista

**Botones de Acción por Vehículo**:
```html
<a th:href="@{/api/vehiculos/{id}(id=${vehiculo.vehiculoId})}"
   class="btn btn-sm btn-outline-primary">
    <i class="fas fa-eye me-1"></i>Detalles
</a>
<a th:href="@{/api/vehiculos/{id}/historial(id=${vehiculo.vehiculoId})}"
   class="btn btn-sm btn-outline-info">
    <i class="fas fa-history me-1"></i>Historial
</a>
```

**Estado Vacío**:
```html
<div th:if="${#lists.isEmpty(vehiculos)}" class="text-center py-5">
    <i class="fas fa-car-side fa-5x text-muted mb-3"></i>
    <h4 class="text-muted">No hay vehículos registrados</h4>
    <p class="text-muted">Comienza registrando el primer vehículo</p>
</div>
```

**Sección de API Endpoints**:
Muestra documentación visual de los endpoints disponibles:
- `GET /api/vehiculos` - Listar todos
- `POST /api/vehiculos/cliente/{clienteId}` - Registrar
- `GET /api/vehiculos/{id}` - Obtener por ID
- `PUT /api/vehiculos/{id}` - Actualizar
- `GET /api/vehiculos/placa/{placa}/estado` - Consultar estado
- `GET /api/vehiculos/{id}/historial` - Historial
- `GET /api/vehiculos/cliente/{clienteId}` - Por cliente

#### Estilos Personalizados:
```css
.vehicle-card {
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
}
.vehicle-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0,0,0,0.2);
}
```

---

### 3. dashboard.html

**Ubicación**: `src/main/resources/templates/dashboard.html`

#### Cambio en Sidebar (línea 40):
```html
<!-- ANTES -->
<a class="nav-link text-white" href="/api/vehiculos"><i class="fas fa-car me-2"></i>Vehículos</a>

<!-- DESPUÉS -->
<a class="nav-link text-white" href="/vehiculos"><i class="fas fa-car me-2"></i>Vehículos</a>
```

**Razón del Cambio**:
- `/api/vehiculos` devuelve JSON (API REST)
- `/vehiculos` muestra la vista HTML (web)
- Consistencia con el patrón de navegación del sistema

---

## Flujo de Datos

```
1. Usuario hace clic en "Vehículos" en el sidebar
       ↓
2. Navegador solicita GET /vehiculos
       ↓
3. WebController.listarVehiculos() ejecuta:
   ├─ vehiculoService.findAll() → Lista de vehículos desde MySQL
   ├─ model.addAttribute("vehiculos", vehiculos)
   ├─ model.addAttribute("totalVehiculos", vehiculos.size())
   └─ return "vehiculos"
       ↓
4. Thymeleaf renderiza vehiculos.html con los datos
       ↓
5. Se muestra la página con:
   ├─ Estadísticas por tipo (calculadas con Thymeleaf)
   ├─ Grid de tarjetas de vehículos
   ├─ Información detallada de cada vehículo
   └─ Botones de acción
       ↓
6. Usuario ve la lista completa de vehículos
```

---

## Tipos de Vehículo y Colores

| Tipo       | Color de Card | Icono     |
|------------|---------------|-----------|
| SEDAN      | Azul (primary) | fa-car-side |
| SUV        | Amarillo (warning) | fa-truck |
| CAMIONETA  | Amarillo (warning) | fa-truck |
| FURGONETA  | Celeste (info) | fa-shuttle-van |
| Otros      | Gris (secondary) | fa-car |

---

## API Existente (VehiculoController.java)

La API REST ya existía y funciona correctamente:

**Endpoints Disponibles**:
```java
GET    /api/vehiculos                         // Listar todos
GET    /api/vehiculos/{id}                    // Obtener por ID
POST   /api/vehiculos/cliente/{clienteId}     // Registrar nuevo
PUT    /api/vehiculos/{id}                    // Actualizar
GET    /api/vehiculos/placa/{placa}/estado    // Consultar estado por placa
GET    /api/vehiculos/{id}/historial          // Ver historial de servicios
GET    /api/vehiculos/cliente/{clienteId}     // Vehículos por cliente
```

**Respuesta Típica** (GET /api/vehiculos):
```json
{
  "success": true,
  "message": "Vehículos obtenidos exitosamente",
  "data": [
    {
      "vehiculoId": 1,
      "placa": "ABC-123",
      "marca": "Toyota",
      "modelo": "Corolla",
      "anioFabricacion": 2020,
      "color": "Rojo",
      "kilometraje": 50000,
      "tipo": "SEDAN",
      "tipoMotor": "GASOLINA",
      "cliente": {
        "clienteId": 1,
        "nombres": "Juan",
        "apellidos": "Pérez"
      }
    }
  ]
}
```

---

## URLs del Sistema

### Vistas Web (HTML):
- `http://localhost:9091/` - Página de inicio
- `http://localhost:9091/login` - Login
- `http://localhost:9091/dashboard` - Dashboard principal
- `http://localhost:9091/vehiculos` - **NUEVA Vista de vehículos**
- `http://localhost:9091/clientes/{id}` - Detalle de cliente
- `http://localhost:9091/clientes/{id}/vehiculos` - Vehículos de un cliente

### API REST (JSON):
- `http://localhost:9091/api/vehiculos` - API de vehículos
- `http://localhost:9091/api/clientes` - API de clientes
- `http://localhost:9091/api/ordenes` - API de órdenes

---

## Integración con el Sistema Existente

### Patrón MVC Implementado:

**Model**:
- Entidad: `Vehiculo.java`
- Service: `VehiculoService.java`
- Repository: `VehiculoDAO.java` (JPA)

**View**:
- Template: `vehiculos.html` (Thymeleaf)
- Bootstrap 5 + Font Awesome 6

**Controller**:
- REST: `VehiculoController.java` (JSON API)
- Web: `WebController.listarVehiculos()` (HTML)

---

## Tecnologías Utilizadas

- **Backend**: Spring Boot 3.5.5
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5.3.0
- **Iconos**: Font Awesome 6.0.0
- **Base de Datos**: MySQL AWS RDS
- **ORM**: Hibernate/JPA

---

## Ventajas de la Implementación

1. **Consistencia**: Sigue el mismo patrón que las vistas de clientes
2. **Responsive**: Funciona en desktop, tablet y móvil
3. **Interactiva**: Efectos hover, transiciones suaves
4. **Informativa**: Estadísticas en tiempo real
5. **Extensible**: Fácil agregar nuevas funcionalidades
6. **Documentada**: Muestra endpoints disponibles
7. **Null-Safe**: Manejo seguro de valores nulos
8. **User-Friendly**: Estado vacío con mensaje claro

---

## Próximas Mejoras Sugeridas

### 1. Búsqueda y Filtros
```html
<input type="text" id="searchPlaca" placeholder="Buscar por placa...">
<select id="filterTipo">
    <option value="">Todos los tipos</option>
    <option value="SEDAN">Sedán</option>
    <option value="SUV">SUV</option>
</select>
```

### 2. Paginación
```java
@GetMapping("/vehiculos")
public String listarVehiculos(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "12") int size,
    Model model
) {
    Page<Vehiculo> vehiculosPage = vehiculoService.findAll(PageRequest.of(page, size));
    // ...
}
```

### 3. Ordenamiento
- Por placa (alfabético)
- Por kilometraje (menor/mayor)
- Por año (más nuevo/antiguo)

### 4. Vista Detalle de Vehículo
Crear `vehiculo-detalle.html` similar a `cliente-detalle.html`:
```java
@GetMapping("/vehiculos/{id}")
public String verDetalleVehiculo(@PathVariable("id") Long id, Model model) {
    // ...
}
```

### 5. Formularios
- Registrar nuevo vehículo (modal o página separada)
- Editar vehículo existente
- Asignar vehículo a cliente

### 6. Gráficos
- Distribución por tipo (pie chart)
- Vehículos por año
- Promedio de kilometraje

---

## Verificación de Funcionamiento

### Pasos para Probar:

1. **Iniciar la aplicación**:
   ```bash
   ./gradlew bootRun
   ```

2. **Acceder al dashboard**:
   - URL: `http://localhost:9091/login`
   - Usuario: `admin`
   - Contraseña: `admin123`

3. **Navegar a Vehículos**:
   - Click en "Vehículos" en el sidebar
   - URL: `http://localhost:9091/vehiculos`

4. **Verificar funcionalidades**:
   - ✅ Se muestran las estadísticas por tipo
   - ✅ Se despliegan las tarjetas de vehículos
   - ✅ Cada vehículo muestra información completa
   - ✅ Los botones "Detalles" y "Historial" funcionan
   - ✅ El diseño es responsive

5. **Probar API REST** (opcional):
   ```bash
   curl http://localhost:9091/api/vehiculos
   ```

---

## Archivos Modificados/Creados

### Archivos Nuevos:
- ✅ `src/main/resources/templates/vehiculos.html`
- ✅ `IMPLEMENTACION_VISTA_VEHICULOS.md` (este documento)

### Archivos Modificados:
- ✅ `src/main/groovy/com/example/autofixpro/controller/WebController.java`
  - Agregado import Vehiculo
  - Agregado método listarVehiculos()
- ✅ `src/main/resources/templates/dashboard.html`
  - Actualizado enlace del sidebar: `/vehiculos`

---

## Notas Técnicas

### Expresiones Thymeleaf Utilizadas:

**Iteración**:
```html
<div th:each="vehiculo : ${vehiculos}">
```

**Texto dinámico**:
```html
<span th:text="${vehiculo.placa}">ABC-123</span>
```

**URLs dinámicas**:
```html
<a th:href="@{/api/vehiculos/{id}(id=${vehiculo.vehiculoId})}">
```

**Condicionales**:
```html
<div th:if="${#lists.isEmpty(vehiculos)}">
```

**Clases CSS condicionales**:
```html
<div th:classappend="${vehiculo.tipo == 'SEDAN'} ? 'bg-primary' : 'bg-warning'">
```

**Expresiones lambda para filtrar**:
```html
th:text="${#lists.size(#lists.select(vehiculos, v -> v.tipo == 'SEDAN'))}"
```

**Operador ternario para null-safety**:
```html
<span th:text="${vehiculo.cliente != null ? vehiculo.cliente.nombres + ' ' + vehiculo.cliente.apellidos : 'No asignado'}">
```

---

## Compatibilidad

- **Navegadores**: Chrome, Firefox, Safari, Edge (últimas versiones)
- **Resoluciones**: Desktop (1920px+), Tablet (768px-1024px), Móvil (320px-767px)
- **Spring Boot**: 3.5.5
- **Java**: 17+
- **MySQL**: 8.0+

---

**Autor**: AutoFixPro Development Team
**Fecha**: 2025-10-18
**Versión**: 1.0
