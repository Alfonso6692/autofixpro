# 🔧 SOLUCIÓN AL PROBLEMA DE LOGIN - AutoFixPro

## 🔍 Diagnóstico del Problema

El problema más común cuando no puedes iniciar sesión es que **la contraseña en la base de datos NO está encriptada con BCrypt**.

Spring Security requiere que las contraseñas estén encriptadas con BCrypt (formato: `$2a$10$...`)

## ✅ SOLUCIÓN RÁPIDA (Recomendada)

### Paso 1: Conectar a MySQL

Abre tu terminal o MySQL Workbench y conéctate a la base de datos:

```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 autofixpro
```

O usa **MySQL Workbench** con estos datos:
- **Host**: `prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com`
- **Port**: `3306`
- **User**: `admin`
- **Password**: `cienpies92`
- **Database**: `autofixpro`

### Paso 2: Verificar el Estado Actual

Ejecuta este comando para ver tus usuarios:

```sql
USE autofixpro;

SELECT
    username,
    nombre,
    role,
    activo,
    CASE
        WHEN password LIKE '$2a$%' THEN '✅ BCrypt OK'
        WHEN password LIKE '$2b$%' THEN '✅ BCrypt OK'
        ELSE '❌ NO es BCrypt'
    END AS 'Estado Password'
FROM usuarios;
```

**Si ves "❌ NO es BCrypt"** → Este es tu problema.

### Paso 3: Aplicar la Solución

Ejecuta el archivo `solucionar-login.sql` que acabo de crear:

```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 autofixpro < solucionar-login.sql
```

O **copia y pega** el contenido de `solucionar-login.sql` en MySQL Workbench y ejecútalo.

### Paso 4: Probar el Login

Después de ejecutar el script SQL, ve a:

```
http://localhost:9091/login
```

**Credenciales de prueba:**

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin123` | Administrador |
| `tecnico1` | `admin123` | Técnico |
| `recepcion` | `admin123` | Recepcionista |
| `cliente1` | `admin123` | Cliente |

## 📝 OPCIÓN ALTERNATIVA: Actualizar SOLO tu Usuario

Si solo quieres arreglar TU usuario específico:

### 1. Identifica tu nombre de usuario

```sql
SELECT username, nombre FROM usuarios;
```

### 2. Actualiza tu contraseña con el hash BCrypt

Reemplaza `'tu_usuario'` con tu nombre de usuario real:

```sql
UPDATE usuarios
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    activo = TRUE
WHERE username = 'tu_usuario';
```

Este hash corresponde a la contraseña: **`admin123`**

### 3. Verifica el cambio

```sql
SELECT username, activo,
    CASE
        WHEN password LIKE '$2a$%' THEN 'OK'
        ELSE 'ERROR'
    END AS estado
FROM usuarios
WHERE username = 'tu_usuario';
```

### 4. Inicia sesión

- **Usuario**: `tu_usuario`
- **Contraseña**: `admin123`

## 🔐 ¿Quieres una contraseña diferente?

Si quieres usar una contraseña diferente a `admin123`, tienes dos opciones:

### Opción A: Usar el formulario de registro

1. Ve a: `http://localhost:9091/register`
2. Crea un nuevo usuario
3. El sistema automáticamente encriptará la contraseña con BCrypt

### Opción B: Generar el hash manualmente

Crea un archivo Java temporal:

```java
// GenerarMiPassword.java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarMiPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String miPassword = "mi_contraseña_secreta"; // Cambia esto
        String hash = encoder.encode(miPassword);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("\nSQL:");
        System.out.println("UPDATE usuarios SET password = '" + hash + "' WHERE username = 'tu_usuario';");
    }
}
```

Compila y ejecuta desde la carpeta del proyecto:

```bash
# En Windows
javac -cp "build\libs\*" GenerarMiPassword.java
java -cp ".;build\libs\*" GenerarMiPassword

# En Linux/Mac
javac -cp "build/libs/*" GenerarMiPassword.java
java -cp ".:build/libs/*" GenerarMiPassword
```

## ⚠️ Errores Comunes

### Error 1: "Usuario o contraseña incorrectos"

**Causas:**
1. ✅ La contraseña NO está encriptada con BCrypt
2. ✅ El usuario está inactivo (`activo = FALSE`)
3. ✅ El usuario no existe en la base de datos

**Solución:** Ejecuta `solucionar-login.sql`

### Error 2: No puedo conectarme a la base de datos

**Verifica:**
```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 -e "SELECT 1"
```

Si falla, verifica:
- Tu conexión a internet
- Las credenciales de AWS RDS
- Los permisos del security group en AWS

### Error 3: El campo 'password' está vacío

**Solución:**
```sql
UPDATE usuarios
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE password IS NULL OR password = '';
```

## 🎯 Resumen de Comandos Rápidos

**Conectar a MySQL:**
```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 autofixpro
```

**Solucionar todo de una vez:**
```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 autofixpro < solucionar-login.sql
```

**Verificar usuarios:**
```sql
SELECT username, role, activo FROM usuarios;
```

## 📞 Necesitas Más Ayuda?

Si después de seguir estos pasos aún no puedes iniciar sesión:

1. Revisa los logs de la aplicación:
   ```bash
   ./gradlew bootRun
   ```
   Busca líneas que digan "Authentication failed" o "User not found"

2. Activa el logging de SQL para ver las queries:
   - Verifica que `spring.jpa.show-sql=true` esté en `application.properties`

3. Comparte el error específico que ves en pantalla o en los logs

---

**Última actualización:** 2025-10-21
**Versión:** 1.0
