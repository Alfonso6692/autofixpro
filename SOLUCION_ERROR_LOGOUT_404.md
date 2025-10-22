# 🔧 Solución: Error 404 en Logout

## ❌ Problema Encontrado

Al intentar cerrar sesión, se obtenía el siguiente error:

```json
{
  "error": true,
  "message": "Endpoint no encontrado: /logout",
  "status": 404,
  "timestamp": "2025-10-17T19:58:05.321948"
}
```

## 🔍 Causa del Problema

### Código Original (INCORRECTO):

**Ubicación**: `dashboard.html:65`

```html
<li><a class="dropdown-item" href="/logout"><i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión</a></li>
```

### ¿Por qué fallaba?

1. **Método HTTP incorrecto**: El enlace `<a href="/logout">` genera una petición **GET**
2. **Spring Security requiere POST**: Por defecto, Spring Security configura el logout para aceptar solo peticiones **POST** por razones de seguridad (protección CSRF)
3. **Sin token CSRF**: La petición GET no incluye el token CSRF necesario para validar la solicitud

### Configuración de Spring Security (SecurityConfig.java:70-76):

```java
.logout(logout -> logout
    .logoutUrl("/logout")                    // Espera POST a /logout
    .logoutSuccessUrl("/login?logout=true")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .permitAll()
)
```

**Nota**: Spring Security por defecto requiere POST para logout desde la versión 4.0 en adelante para prevenir ataques CSRF.

---

## ✅ Solución Implementada

### Código Corregido:

**Ubicación**: `dashboard.html:65-72`

```html
<li>
    <form method="post" action="/logout" style="display: inline;">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
        <button type="submit" class="dropdown-item" style="cursor: pointer; border: none; background: none; width: 100%; text-align: left;">
            <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
        </button>
    </form>
</li>
```

### Cambios Realizados:

1. ✅ **Formulario POST**: Cambió de `<a href>` a `<form method="post">`
2. ✅ **Token CSRF incluido**: Añadido campo oculto con el token CSRF de Thymeleaf
3. ✅ **Botón estilizado**: El botón mantiene la apariencia de un elemento del dropdown
4. ✅ **Funcionalidad correcta**: Ahora el logout funciona correctamente

---

## 🎨 Detalles de Estilo

El botón se ha estilizado para verse exactamente como un elemento de dropdown de Bootstrap:

```css
style="cursor: pointer; border: none; background: none; width: 100%; text-align: left;"
```

Esto asegura que:
- ✓ No tiene borde visible
- ✓ No tiene fondo (usa el de Bootstrap)
- ✓ Ocupa todo el ancho del dropdown
- ✓ El texto está alineado a la izquierda
- ✓ El cursor cambia a pointer al pasar sobre él

---

## 📝 Flujo Completo del Logout

### Paso 1: Usuario hace clic en "Cerrar Sesión"

```html
<button type="submit" class="dropdown-item">
    <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
</button>
```

### Paso 2: Se envía petición POST con CSRF

```http
POST /logout HTTP/1.1
Host: localhost:9091
Content-Type: application/x-www-form-urlencoded
Cookie: JSESSIONID=1D814E4D6F16E49D98547150E98EA768

_csrf=S2WuOp9tG3gfJq3pIQkHOV4Sc-alc2zM2r6-vb5nGoonklDBc12WX6xUekEyR5XRQCQzCTgqXoedQV3huNrdj9pSLrxFpTHy
```

### Paso 3: Spring Security procesa logout

```java
// SecurityConfig.java:70-76
.logout(logout -> logout
    .logoutUrl("/logout")                    // ✓ Valida URL
    .logoutSuccessUrl("/login?logout=true")  // → Redirige aquí
    .invalidateHttpSession(true)             // ✓ Destruye sesión
    .deleteCookies("JSESSIONID")            // ✓ Elimina cookie
    .permitAll()
)
```

### Paso 4: Redirección exitosa

```http
HTTP/1.1 302 Found
Location: /login?logout=true
Set-Cookie: JSESSIONID=deleted; Path=/; Max-Age=0
```

### Paso 5: Usuario ve mensaje de logout

En `AuthController.java:35-37`:

```java
if (logout != null) {
    model.addAttribute("message", "Sesión cerrada exitosamente");
}
```

---

## 🔐 Seguridad del Logout

### ¿Por qué POST en lugar de GET?

#### Ejemplo de Ataque con GET (Vulnerable):

```html
<!-- Sitio malicioso: evil.com -->
<img src="http://tuapp.com/logout" />
```

Si el logout fuera GET, simplemente cargar esta imagen cerraría la sesión del usuario sin su consentimiento.

#### Protección con POST + CSRF:

```html
<!-- Sitio malicioso: evil.com -->
<form action="http://tuapp.com/logout" method="post">
    <input type="hidden" name="_csrf" value="token-desconocido-por-atacante"/>
</form>
<script>document.forms[0].submit();</script>
```

❌ **Falla** porque:
1. El atacante no conoce el token CSRF del usuario
2. Spring Security rechaza la petición sin token válido
3. El usuario permanece autenticado

---

## 🛠️ Soluciones Alternativas

### Opción 1: Usar Thymeleaf `th:action` (Más elegante)

```html
<li>
    <form th:action="@{/logout}" method="post" style="display: inline;">
        <!-- Token CSRF se agrega automáticamente -->
        <button type="submit" class="dropdown-item">
            <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
        </button>
    </form>
</li>
```

**Ventaja**: Thymeleaf agrega automáticamente el token CSRF cuando usas `th:action`.

### Opción 2: Permitir GET (NO RECOMENDADO - Inseguro)

```java
// SecurityConfig.java
.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // ⚠️ INSEGURO
    .logoutSuccessUrl("/login?logout=true")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .permitAll()
)
```

❌ **NO usar esta opción** - Es vulnerable a ataques CSRF.

### Opción 3: Crear endpoint personalizado en el controlador

```java
// AuthController.java
@PostMapping("/logout")
public String logout(HttpServletRequest request) {
    request.getSession().invalidate();
    return "redirect:/login?logout=true";
}
```

❌ **NO recomendado** - Duplica lógica que Spring Security ya maneja.

---

## ✅ Verificación de la Solución

### Prueba Manual:

1. Inicia sesión en `http://localhost:9091/login`
   - Usuario: `admin`
   - Contraseña: `admin123`

2. Una vez en el dashboard, haz clic en el menú de usuario

3. Selecciona "Cerrar Sesión"

4. **Resultado esperado**:
   - ✅ Redirección a `/login?logout=true`
   - ✅ Mensaje: "Sesión cerrada exitosamente"
   - ✅ Cookie JSESSIONID eliminada
   - ✅ No puede acceder a `/dashboard` sin volver a autenticarse

### Prueba con cURL:

```bash
# 1. Login
curl -c cookies.txt -s http://localhost:9091/login -o login.html
CSRF=$(grep '_csrf' login.html | grep -oP 'value="[^"]*"' | head -1 | sed 's/value="//;s/"$//')

curl -X POST http://localhost:9091/login \
  -d "username=admin&password=admin123&_csrf=$CSRF" \
  -b cookies.txt -c cookies.txt -L

# 2. Logout
CSRF=$(grep '_csrf' dashboard.html | grep -oP 'value="[^"]*"' | head -1 | sed 's/value="//;s/"$//')

curl -X POST http://localhost:9091/logout \
  -d "_csrf=$CSRF" \
  -b cookies.txt -c cookies.txt -w "\nHTTP Status: %{http_code}\n"

# Resultado esperado: HTTP Status: 302 (Redirección)
```

---

## 📚 Referencias

- [Spring Security Logout Documentation](https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html)
- [OWASP CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Thymeleaf + Spring Security Integration](https://www.thymeleaf.org/doc/articles/springsecurity.html)

---

## 📊 Resumen del Cambio

| Aspecto | Antes (❌) | Después (✅) |
|---------|----------|------------|
| **Método HTTP** | GET | POST |
| **Token CSRF** | No incluido | Incluido |
| **Seguridad** | Vulnerable a CSRF | Protegido contra CSRF |
| **Resultado** | Error 404 | Logout exitoso |
| **Código** | `<a href="/logout">` | `<form method="post">` con token |

---

**Archivo modificado**: `src/main/resources/templates/dashboard.html` (líneas 65-72)

**Fecha de corrección**: 2025-10-17

**Autor**: AutoFixPro Development Team