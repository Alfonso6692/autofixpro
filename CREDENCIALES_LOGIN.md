# 🔐 CREDENCIALES PARA LOGIN - AutoFixPro

## 📍 URL de Login
```
http://localhost:9091/login
```

## ✅ CREDENCIALES ACTUALES

La aplicación está corriendo en el puerto **9091**.

### Opción 1: Registrar un nuevo usuario

1. Ve a: http://localhost:9091/register
2. Completa el formulario de registro
3. La contraseña debe tener mínimo 6 caracteres
4. Una vez registrado, vuelve a login

### Opción 2: Insertar usuarios manualmente en la base de datos

**IMPORTANTE**: Los usuarios deben existir en la base de datos MySQL.

Ejecuta este comando desde tu cliente MySQL o MySQL Workbench:

```sql
USE autofixpro;

-- Eliminar usuarios existentes si los hay
DELETE FROM usuarios WHERE username IN ('admin', 'tecnico1', 'recepcion', 'cliente1');

-- Insertar usuarios con contraseña: admin123
INSERT INTO usuarios (username, password, nombre, email, telefono, role, activo, fecha_creacion, ultima_actualizacion)
VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Administrador del Sistema', 'admin@autofixpro.com', '+51999999999', 'ADMIN', TRUE, NOW(), NOW()),
('tecnico1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'José Luis Ramírez', 'jose_luis@outlook.com', '+51965409978', 'TECNICO', TRUE, NOW(), NOW()),
('recepcion', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'María Recepción', 'recepcion@autofixpro.com', '+51988888888', 'RECEPCIONISTA', TRUE, NOW(), NOW()),
('cliente1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Juan Pérez García', 'juan.perez@email.com', '+51987654321', 'USER', TRUE, NOW(), NOW());

-- Verificar que se insertaron
SELECT username, nombre, role, activo FROM usuarios;
```

### Credenciales después de insertar:

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin123` | ADMIN |
| `tecnico1` | `admin123` | TECNICO |
| `recepcion` | `admin123` | RECEPCIONISTA |
| `cliente1` | `admin123` | USER |

## 🔧 Conectar a MySQL

```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 autofixpro
```

O usa MySQL Workbench con estos datos:
- **Host**: prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com
- **Port**: 3306
- **User**: admin
- **Password**: cienpies92
- **Database**: autofixpro

## 📝 Opción 3: Usar DataLoader automático

La aplicación tiene un `DataLoader` que creará usuarios automáticamente si la tabla `usuarios` está vacía.

Para que funcione:

1. Detén la aplicación actual (Ctrl+C en la terminal donde corre)
2. Vacía la tabla usuarios:
   ```sql
   DELETE FROM usuarios;
   ```
3. Reinicia la aplicación:
   ```bash
   ./gradlew bootRun
   ```
4. El DataLoader detectará que no hay usuarios y los creará automáticamente

Verás este mensaje en la consola:
```
========================================
🔐 Creando usuarios iniciales...
========================================
✅ Usuario ADMIN creado: admin / admin123
✅ Usuario TECNICO creado: tecnico1 / admin123
✅ Usuario RECEPCIONISTA creado: recepcion / admin123
✅ Usuario USER creado: cliente1 / admin123
========================================
```

## ⚠️ Solución de Problemas

### Error: "Usuario o contraseña incorrectos"

1. Verifica que los usuarios existan en la BD:
   ```sql
   SELECT * FROM usuarios;
   ```

2. Si la tabla está vacía, usa una de las 3 opciones anteriores

3. Asegúrate de estar usando el puerto correcto: **9091** (no 8080)

### No puedo conectarme a la base de datos

Verifica tu conexión con:
```bash
mysql -h prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com -u admin -pcienpies92 -e "SELECT 1"
```

---

**Última actualización**: 2025-10-10