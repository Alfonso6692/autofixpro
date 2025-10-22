# 🔐 Seguridad CSRF en AutoFixPro

## 📚 Índice
1. [¿Qué es CSRF?](#qué-es-csrf)
2. [¿Cómo funciona un ataque CSRF?](#cómo-funciona-un-ataque-csrf)
3. [Protección CSRF en Spring Security](#protección-csrf-en-spring-security)
4. [Implementación en AutoFixPro](#implementación-en-autofixpro)
5. [Ejemplo Práctico](#ejemplo-práctico)
6. [Buenas Prácticas](#buenas-prácticas)

---

## ¿Qué es CSRF?

**CSRF** (Cross-Site Request Forgery) o **Falsificación de Petición en Sitios Cruzados** es un tipo de ataque de seguridad web donde un atacante engaña a un usuario autenticado para que ejecute acciones no deseadas en una aplicación web en la que está autenticado.

### 🎯 Objetivo del Ataque
Hacer que un usuario legítimo ejecute acciones sin su consentimiento:
- Transferir dinero
- Cambiar contraseña
- Eliminar datos
- Modificar configuraciones
- Crear/eliminar usuarios

### 🔑 Conceptos Clave

**Token CSRF**: Un valor único, secreto e impredecible que se genera por el servidor y se asocia con la sesión del usuario. Este token debe ser incluido en cada petición que modifique datos (POST, PUT, DELETE, PATCH).

**Validación**: El servidor verifica que el token CSRF recibido en la petición coincida con el token almacenado en la sesión del usuario.

---

## ¿Cómo funciona un ataque CSRF?

### Escenario de Ataque (SIN protección CSRF)

```
1. Usuario (Juan) → Inicia sesión en banco.com
   ✅ Cookie de sesión: JSESSIONID=abc123

2. Usuario (Juan) → Visita sitio-malicioso.com (sin cerrar sesión del banco)

3. sitio-malicioso.com → Contiene código malicioso:
   <form action="https://banco.com/transferir" method="POST">
     <input type="hidden" name="destinatario" value="atacante">
     <input type="hidden" name="monto" value="1000">
   </form>
   <script>document.forms[0].submit();</script>

4. Navegador de Juan → Automáticamente envía la petición a banco.com
   ⚠️  Incluye la cookie de sesión válida (JSESSIONID=abc123)

5. banco.com → Recibe petición con sesión válida
   ❌ Sin protección CSRF: Ejecuta la transferencia
   ✅ Con protección CSRF: Rechaza la petición (falta token CSRF)
```

### ¿Por qué funciona?

El navegador **automáticamente** incluye las cookies asociadas al dominio en cada petición, incluso si la petición se origina desde otro sitio.

---

## Protección CSRF en Spring Security

Spring Security implementa protección CSRF por defecto mediante **Synchronizer Token Pattern**.

### 🛡️ Flujo de Protección

```
1. GET /login
   ← Servidor genera: Token CSRF = "abc123xyz..."
   ← Respuesta incluye:
     - Cookie de sesión
     - Token CSRF en campo oculto del formulario

2. Usuario → Completa formulario y envía

3. POST /login
   → Incluye:
     - Cookie de sesión
     - Token CSRF en parámetro _csrf

4. Servidor → Valida:
   ✓ ¿Cookie de sesión válida?
   ✓ ¿Token CSRF coincide con la sesión?

   → Si ambos son válidos: ✅ Procesa petición
   → Si falta token o no coincide: ❌ Rechaza (403 Forbidden)
```

### 🔒 Características del Token CSRF

- **Único**: Cada sesión tiene su propio token
- **Secreto**: No puede ser adivinado por un atacante
- **Por sesión**: Se almacena en la sesión del servidor
- **No en cookies**: No se envía automáticamente como las cookies

---

## Implementación en AutoFixPro

### 1. Configuración en SecurityConfig.java

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            // Deshabilitado para ciertas rutas
            .ignoringRequestMatchers("/h2-console/**", "/api/**")
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")  // Spring Security maneja CSRF automáticamente
            .defaultSuccessUrl("/dashboard", true)
            .failureUrl("/login?error=true")
            .usernameParameter("username")
            .passwordParameter("password")
            .permitAll()
        );

    return http.build();
}
```

**Líneas 47-85**: Spring Security valida CSRF automáticamente en formularios de login.

### 2. Template de Login (login.html)

```html
<form action="/login" method="post">
    <!-- Token CSRF generado automáticamente por Thymeleaf -->
    <input type="hidden" name="_csrf"
           value="S2WuOp9tG3gfJq3pIQkHOV4Sc-alc2zM2r6-vb5nGoonklDBc12WX6xUekEyR5XRQCQzCTgqXoedQV3huNrdj9pSLrxFpTHy"/>

    <div class="form-group">
        <input type="text" name="username" placeholder="Usuario" required>
    </div>

    <div class="form-group">
        <input type="password" name="password" placeholder="Contraseña" required>
    </div>

    <button type="submit">Iniciar Sesión</button>
</form>
```

**Campo oculto `_csrf`**: Contiene el token que será validado por Spring Security.

### 3. Thymeleaf Automático

Si usas Thymeleaf con `th:action`, el token se incluye automáticamente:

```html
<!-- Forma recomendada con Thymeleaf -->
<form th:action="@{/login}" method="post">
    <!-- No necesitas agregar manualmente el campo _csrf -->
    <input type="text" name="username"/>
    <input type="password" name="password"/>
    <button type="submit">Login</button>
</form>
```

Thymeleaf genera automáticamente:
```html
<input type="hidden" name="_csrf" value="token-generado-aqui"/>
```

### 4. AJAX con CSRF Token

Para peticiones AJAX, debes incluir el token manualmente:

```javascript
// Opción 1: Leer token del meta tag
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>

<script>
var token = document.querySelector('meta[name="_csrf"]').content;
var header = document.querySelector('meta[name="_csrf_header"]').content;

fetch('/api/endpoint', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        [header]: token  // Incluir token en header
    },
    body: JSON.stringify({data: 'value'})
});
</script>

// Opción 2: Incluir en el body
fetch('/api/endpoint', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: '_csrf=' + token + '&data=value'
});
```

---

## Ejemplo Práctico

### Flujo Completo de Login en AutoFixPro

#### Paso 1: Usuario visita /login

```http
GET http://localhost:9091/login HTTP/1.1
```

**Respuesta del servidor:**
```http
HTTP/1.1 200 OK
Set-Cookie: JSESSIONID=1D814E4D6F16E49D98547150E98EA768; Path=/; HttpOnly

<!DOCTYPE html>
<html>
<body>
    <form action="/login" method="post">
        <input type="hidden" name="_csrf"
               value="S2WuOp9tG3gfJq3pIQkHOV4Sc-alc2zM2r6-vb5nGoonklDBc12WX6xUekEyR5XRQCQzCTgqXoedQV3huNrdj9pSLrxFpTHy"/>
        <input type="text" name="username"/>
        <input type="password" name="password"/>
        <button type="submit">Login</button>
    </form>
</body>
</html>
```

**Almacenado en el servidor:**
- Sesión: `1D814E4D6F16E49D98547150E98EA768`
- Token CSRF: `S2WuOp9tG3gfJq3pIQkHOV4Sc-alc2zM2r6-vb5nGoonklDBc12WX6xUekEyR5XRQCQzCTgqXoedQV3huNrdj9pSLrxFpTHy`

#### Paso 2: Usuario envía credenciales

```http
POST http://localhost:9091/login HTTP/1.1
Cookie: JSESSIONID=1D814E4D6F16E49D98547150E98EA768
Content-Type: application/x-www-form-urlencoded

username=admin&password=admin123&_csrf=S2WuOp9tG3gfJq3pIQkHOV4Sc-alc2zM2r6-vb5nGoonklDBc12WX6xUekEyR5XRQCQzCTgqXoedQV3huNrdj9pSLrxFpTHy
```

**Validación en el servidor (Spring Security):**

```java
// 1. Validar sesión
Session session = getSession(JSESSIONID);
if (session == null) return 403; // ❌ Sesión inválida

// 2. Validar token CSRF
String receivedToken = request.getParameter("_csrf");
String expectedToken = session.getAttribute("CSRF_TOKEN");

if (!receivedToken.equals(expectedToken)) {
    return 403; // ❌ Token CSRF inválido
}

// 3. Validar credenciales
if (validarCredenciales(username, password)) {
    return redirect("/dashboard"); // ✅ Login exitoso
} else {
    return redirect("/login?error=true"); // ❌ Credenciales incorrectas
}
```

**Respuesta exitosa:**
```http
HTTP/1.1 302 Found
Location: http://localhost:9091/dashboard
Set-Cookie: JSESSIONID=1D814E4D6F16E49D98547150E98EA768; Path=/; HttpOnly
```

#### Paso 3: Intento de ataque CSRF (bloqueado)

Un sitio malicioso intenta:
```html
<!-- sitio-malicioso.com -->
<form action="http://localhost:9091/api/usuarios/delete/1" method="POST">
    <input type="hidden" name="id" value="1">
</form>
<script>document.forms[0].submit();</script>
```

**Petición enviada:**
```http
POST http://localhost:9091/api/usuarios/delete/1 HTTP/1.1
Cookie: JSESSIONID=1D814E4D6F16E49D98547150E98EA768
Content-Type: application/x-www-form-urlencoded

id=1
```

**Resultado:**
```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "error": "Forbidden",
  "message": "Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'."
}
```

❌ **Ataque bloqueado** porque falta el token CSRF.

---

## Buenas Prácticas

### ✅ Recomendaciones

1. **Mantener CSRF habilitado**: No deshabilitar CSRF a menos que sea absolutamente necesario.

```java
// ❌ MAL - Deshabilitar completamente
http.csrf().disable();

// ✅ BIEN - Deshabilitar solo para APIs REST stateless
http.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/**")  // Solo APIs sin estado
);
```

2. **Usar Thymeleaf para formularios**: El token se incluye automáticamente.

```html
<!-- ✅ BIEN -->
<form th:action="@{/actualizar-perfil}" method="post">
    <!-- Token CSRF agregado automáticamente -->
</form>

<!-- ❌ MAL -->
<form action="/actualizar-perfil" method="post">
    <!-- Falta token CSRF -->
</form>
```

3. **SameSite cookies**: Configurar cookies con atributo `SameSite` para protección adicional.

```java
@Bean
public CookieSameSiteSupplier cookieSameSiteSupplier() {
    return CookieSameSiteSupplier.ofStrict(); // SameSite=Strict
}
```

4. **Validar origen**: Verificar headers `Origin` y `Referer`.

```java
http.csrf(csrf -> csrf
    .requireCsrfProtectionMatcher(new RequestMatcher() {
        @Override
        public boolean matches(HttpServletRequest request) {
            String referer = request.getHeader("Referer");
            return referer == null || !referer.startsWith("https://tudominio.com");
        }
    })
);
```

5. **HTTPS siempre**: Usar HTTPS para evitar que el token sea interceptado.

```properties
# application.properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=tu-password
server.ssl.key-store-type=PKCS12
```

### ⚠️ Errores Comunes

1. **Olvidar incluir el token en AJAX**
```javascript
// ❌ Error común
fetch('/api/actualizar', {
    method: 'POST',
    body: JSON.stringify({data: 'value'})
});

// ✅ Correcto
var token = document.querySelector('meta[name="_csrf"]').content;
fetch('/api/actualizar', {
    method: 'POST',
    headers: {
        'X-CSRF-TOKEN': token
    },
    body: JSON.stringify({data: 'value'})
});
```

2. **Deshabilitar CSRF innecesariamente**
```java
// ❌ MAL - Deshabilita toda la protección
http.csrf().disable();

// ✅ BIEN - Solo deshabilita para APIs REST
http.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/**")
);
```

3. **No usar HTTPS en producción**
```properties
# ❌ MAL - HTTP en producción
server.port=8080

# ✅ BIEN - HTTPS en producción
server.port=443
server.ssl.enabled=true
```

---

## 📊 Comparación: Con vs Sin CSRF

| Aspecto | Sin Protección CSRF | Con Protección CSRF |
|---------|-------------------|-------------------|
| **Ataque desde sitio malicioso** | ❌ Exitoso | ✅ Bloqueado |
| **Complejidad de implementación** | Baja | Media |
| **Seguridad** | Baja | Alta |
| **Experiencia de usuario** | Sin cambios | Sin cambios (transparente) |
| **Performance** | Normal | Muy bajo impacto (~1-2ms) |

---

## 🔍 Debugging CSRF

### Ver token CSRF en Chrome DevTools

1. Abre **DevTools** (F12)
2. Ve a **Network**
3. Recarga la página de login
4. Click en la petición a `/login`
5. Ve a **Response** → Busca `<input type="hidden" name="_csrf"`

### Ver logs de validación CSRF

```properties
# application.properties
logging.level.org.springframework.security.web.csrf=DEBUG
```

**Log de ejemplo:**
```
DEBUG o.s.security.web.csrf.CsrfFilter : Invalid CSRF token found for http://localhost:9091/login
DEBUG o.s.security.web.csrf.CsrfFilter : Expected CSRF token: S2WuOp9tG3gfJq3pIQkHOV4Sc-alc2zM2r6-vb5n...
DEBUG o.s.security.web.csrf.CsrfFilter : Actual CSRF token: null
```

### Probar manualmente con cURL

```bash
# 1. Obtener token CSRF
curl -c cookies.txt http://localhost:9091/login -s | grep _csrf

# Output:
# <input type="hidden" name="_csrf" value="ABC123..."/>

# 2. Intentar login sin token (fallará)
curl -X POST http://localhost:9091/login \
  -d "username=admin&password=admin123" \
  -b cookies.txt

# Output: 403 Forbidden

# 3. Login con token (exitoso)
curl -X POST http://localhost:9091/login \
  -d "username=admin&password=admin123&_csrf=ABC123..." \
  -b cookies.txt -c cookies.txt

# Output: 302 Redirect to /dashboard
```

---

## 📚 Referencias

- [OWASP CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Spring Security CSRF Documentation](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html)
- [RFC 7034 - X-Frame-Options](https://tools.ietf.org/html/rfc7034)

---

**Autor**: AutoFixPro Development Team
**Fecha**: Octubre 2025
**Versión**: 1.0