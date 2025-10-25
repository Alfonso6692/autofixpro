# 🔐 Spring Security - AutoFixPro

## Documentación de Implementación de Seguridad

---

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Configuración de Seguridad](#configuración-de-seguridad)
3. [Roles y Permisos](#roles-y-permisos)
4. [Autenticación y Autorización](#autenticación-y-autorización)
5. [Protección de Endpoints](#protección-de-endpoints)
6. [WebSocket Security](#websocket-security)
7. [Manejo de Sesiones](#manejo-de-sesiones)
8. [Pruebas de Seguridad](#pruebas-de-seguridad)

---

## 🎯 Descripción General

AutoFixPro implementa Spring Security 6.2 con las siguientes características:

- ✅ **Autenticación** basada en usuario/contraseña con BCrypt
- ✅ **Autorización** basada en roles (RBAC)
- ✅ **Protección CSRF** configurada
- ✅ **Seguridad de WebSocket** para notificaciones en tiempo real
- ✅ **Gestión de sesiones** con control de sesiones concurrentes
- ✅ **Páginas personalizadas** de login y access denied

---

## ⚙️ Configuración de Seguridad

### Archivos Principales

1. **`SecurityConfig.java`**
   - Configuración principal de Spring Security
   - Define las reglas de autorización HTTP
   - Configura form login y logout
   - Maneja CSRF y headers

2. **`WebSocketSecurityConfig.java`**
   - Configuración de seguridad para WebSocket/STOMP
   - Protege las conexiones WebSocket
   - Asegura que solo usuarios autenticados reciban notificaciones

3. **`CustomAuthenticationSuccessHandler.java`**
   - Maneja redirecciones post-login según el rol del usuario
   - ADMIN/TECNICO/RECEPCIONISTA → `/dashboard`
   - USER (Clientes) → `/cliente-dashboard`

---

## 👥 Roles y Permisos

### Roles Disponibles

| Rol | Código | Descripción | Acceso |
|-----|--------|-------------|---------|
| **Administrador** | `ROLE_ADMIN` | Control total del sistema | Todos los endpoints |
| **Recepcionista** | `ROLE_RECEPCIONISTA` | Gestión de clientes y órdenes | Dashboard, crear órdenes, consultar |
| **Técnico** | `ROLE_TECNICO` | Actualización de reparaciones | Dashboard, actualizar órdenes |
| **Cliente** | `ROLE_USER` | Consulta de vehículos propios | Cliente dashboard, consultas |

### Jerarquía de Permisos

```
ADMIN
  ├─ Gestión de usuarios
  ├─ Gestión de técnicos
  ├─ Todos los permisos de RECEPCIONISTA
  └─ Todos los permisos de TECNICO

RECEPCIONISTA
  ├─ Crear órdenes de servicio
  ├─ Registrar clientes
  ├─ Registrar vehículos
  └─ Consultar información

TECNICO
  ├─ Actualizar estado de órdenes
  ├─ Consultar órdenes asignadas
  └─ Actualizar progreso de reparaciones

USER (Cliente)
  ├─ Ver sus propios vehículos
  ├─ Consultar estado de órdenes
  └─ Recibir notificaciones en tiempo real
```

---

## 🔑 Autenticación y Autorización

### Flujo de Autenticación

1. **Usuario accede** a `/login`
2. **Ingresa credenciales** (username/password)
3. **Spring Security valida** contra la base de datos
4. **UsuarioService.loadUserByUsername()** carga los detalles del usuario
5. **Contraseña verificada** con BCrypt
6. **Si es exitoso**, `CustomAuthenticationSuccessHandler` redirige según el rol
7. **Sesión creada** con token JSESSIONID

### Encriptación de Contraseñas

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Las contraseñas se encriptan automáticamente al registrar usuarios:

```java
usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
```

---

## 🛡️ Protección de Endpoints

### Reglas de Autorización HTTP

```java
http.authorizeHttpRequests(authz -> authz
    // Públicos
    .requestMatchers("/", "/login", "/register", "/consultar").permitAll()
    .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()

    // WebSocket
    .requestMatchers("/ws-notifications/**").permitAll()

    // API REST (sin autenticación en desarrollo)
    .requestMatchers("/api/**").permitAll()

    // Administrativos
    .requestMatchers("/admin/**").hasRole("ADMIN")

    // Dashboard
    .requestMatchers("/dashboard", "/ordenes/**", "/vehiculos/**")
        .hasAnyRole("ADMIN", "TECNICO", "RECEPCIONISTA")

    // Clientes
    .requestMatchers("/cliente-dashboard", "/cliente/**").hasRole("USER")

    // Técnicos
    .requestMatchers("/tecnico/**").hasRole("TECNICO")

    // Todo lo demás requiere autenticación
    .anyRequest().authenticated()
)
```

### Protección a Nivel de Método

#### Ejemplo 1: Crear Orden de Servicio
```java
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
@PostMapping("/api/ordenes")
public ResponseEntity<Map<String, Object>> crearOrdenServicio(@RequestBody Map<String, Object> request) {
    // Solo ADMIN y RECEPCIONISTA pueden crear órdenes
}
```

#### Ejemplo 2: Gestión de Usuarios
```java
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {
    // Toda la clase requiere rol ADMIN
}
```

---

## 🔌 WebSocket Security

### Configuración

```java
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Permitir conexión
            .simpTypeMatchers(CONNECT, HEARTBEAT, DISCONNECT).permitAll()
            // Solo autenticados pueden enviar mensajes
            .simpDestMatchers("/app/**").authenticated()
            // Solo autenticados reciben notificaciones personales
            .simpSubscribeDestMatchers("/user/queue/**").authenticated()
            // Todo requiere autenticación
            .anyMessage().authenticated();
    }
}
```

### Uso en el Cliente

Las notificaciones se envían solo al usuario propietario del vehículo:

```java
messagingTemplate.convertAndSendToUser(
    username,
    "/queue/notificaciones",
    notificacion
);
```

---

## 💼 Manejo de Sesiones

### Configuración de Sesiones

```java
.sessionManagement(session -> session
    .maximumSessions(1)  // Máximo 1 sesión por usuario
    .maxSessionsPreventsLogin(false)  // Nueva sesión invalida la anterior
)
```

### Logout

```java
.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login?logout=true")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .clearAuthentication(true)
    .permitAll()
)
```

---

## 🧪 Pruebas de Seguridad

### Casos de Prueba

#### 1. **Acceso No Autenticado**
```bash
# Debe redirigir a /login
curl -I http://localhost:9091/dashboard
# Expected: 302 Found → /login
```

#### 2. **Login Exitoso**
```bash
curl -X POST http://localhost:9091/login \
  -d "username=admin&password=admin123" \
  -c cookies.txt

# Verificar sesión
curl -b cookies.txt http://localhost:9091/dashboard
# Expected: 200 OK
```

#### 3. **Acceso Denegado por Rol**
```bash
# Login como USER (cliente)
curl -X POST http://localhost:9091/login \
  -d "username=cliente1&password=pass123" \
  -c cookies.txt

# Intentar acceder al admin panel
curl -b cookies.txt http://localhost:9091/admin/usuarios
# Expected: 403 Forbidden → /access-denied
```

#### 4. **Crear Orden sin Permisos**
```bash
# Login como TECNICO
curl -X POST http://localhost:9091/login \
  -d "username=tecnico1&password=tecnico123" \
  -c cookies.txt

# Intentar crear orden (solo ADMIN/RECEPCIONISTA)
curl -X POST http://localhost:9091/api/ordenes \
  -H "Content-Type: application/json" \
  -d '{"vehiculoId": 1, "descripcionProblema": "test"}' \
  -b cookies.txt
# Expected: 403 Forbidden
```

#### 5. **WebSocket Autenticado**
```javascript
// Conectar sin autenticación
const socket = new SockJS('/ws-notifications');
// Expected: Conexión rechazada o sin recepción de mensajes personales
```

---

## 📊 Matriz de Permisos por Endpoint

| Endpoint | ADMIN | RECEPCIONISTA | TECNICO | USER | Público |
|----------|-------|---------------|---------|------|---------|
| `/login` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/dashboard` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/cliente-dashboard` | ❌ | ❌ | ❌ | ✅ | ❌ |
| `POST /api/ordenes` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `PUT /api/ordenes/{id}` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/usuarios` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `/cliente/orden/{id}` | ❌ | ❌ | ❌ | ✅* | ❌ |
| `/ws-notifications` | ✅ | ✅ | ✅ | ✅ | ❌ |

*Solo puede ver sus propias órdenes

---

## 🚨 Recomendaciones de Seguridad

### Para Desarrollo
- ✅ CSRF habilitado (excepto en H2 console y API)
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Sesiones con timeout automático
- ✅ WebSocket con autenticación

### Para Producción

1. **Restringir API REST**
   ```java
   .requestMatchers("/api/**").authenticated()
   ```

2. **Habilitar HTTPS**
   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=password
   ```

3. **Configurar CORS específico**
   ```java
   @CrossOrigin(origins = "https://autofixpro.com")
   ```

4. **Deshabilitar H2 Console**
   ```properties
   spring.h2.console.enabled=false
   ```

5. **WebSocket Same Origin**
   ```java
   @Override
   protected boolean sameOriginDisabled() {
       return false;  // Cambiar a false en producción
   }
   ```

---

## 📞 Contacto

Para consultas sobre seguridad:
- **Equipo**: AutoFixPro Development Team
- **Email**: security@autofixpro.com

---

**Última actualización**: Octubre 2025
**Versión Spring Security**: 6.2.10
**Versión Spring Boot**: 3.5.5
