# Despliegue de AutoFixPro en Railway

## Pasos para Desplegar

### 1. Preparación del Proyecto
✅ Ya completado - Los archivos necesarios ya están creados:
- `Procfile` - Comando de inicio
- `nixpacks.toml` - Configuración de build
- `application.properties` - Puerto dinámico configurado

### 2. Crear Cuenta en Railway

1. Ve a **https://railway.app**
2. Click en "Start a New Project"
3. Inicia sesión con GitHub (recomendado)

### 3. Subir el Proyecto a GitHub (si aún no lo has hecho)

```bash
# Si no tienes repositorio remoto
git remote add origin https://github.com/TU_USUARIO/autofixpro.git
git branch -M main
git push -u origin main

# Si ya tienes repositorio
git add .
git commit -m "Preparar para despliegue en Railway"
git push
```

### 4. Desplegar en Railway

#### Opción A: Desde GitHub (Recomendado)
1. En Railway, click "Deploy from GitHub repo"
2. Autoriza Railway a acceder a tu cuenta de GitHub
3. Selecciona el repositorio `autofixpro`
4. Railway detectará automáticamente que es un proyecto Java/Gradle
5. Click en "Deploy Now"

#### Opción B: Desde CLI
```bash
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Inicializar proyecto
railway init

# Desplegar
railway up
```

### 5. Configurar Variables de Entorno

En el dashboard de Railway, ve a tu proyecto > Variables y agrega:

**Obligatorias:**
```
PORT=8080
```

**Opcionales (si quieres usar otra base de datos):**
Si quieres usar PostgreSQL de Railway en lugar de tu MySQL de AWS:

1. En Railway, click "New" > "Database" > "Add PostgreSQL"
2. Railway creará automáticamente la variable `DATABASE_URL`
3. Agrega estas variables:
```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

**Para continuar usando tu MySQL de AWS (actual):**
No necesitas agregar nada más, ya está en `application.properties`

### 6. Configuración de Java

Railway detectará automáticamente Java 17 por el archivo `nixpacks.toml`

### 7. Verificar Despliegue

1. Railway te dará una URL como: `https://autofixpro-production.up.railway.app`
2. Espera 2-3 minutos mientras compila y despliega
3. Visita la URL para verificar que funciona

### 8. Ver Logs

En el dashboard de Railway:
- Click en tu proyecto
- Ve a la pestaña "Deployments"
- Click en "View Logs"

## Solución de Problemas

### Error: "Build failed"
- Verifica que `./gradlew` tenga permisos de ejecución
- En local ejecuta: `git update-index --chmod=+x gradlew`

### Error: "Port already in use"
- Asegúrate de que la variable `PORT` esté configurada en Railway
- Verifica que `application.properties` tenga `server.port=${PORT:9091}`

### Error: "Cannot connect to database"
- Verifica las credenciales de tu base de datos MySQL en AWS
- Asegúrate de que el security group de AWS RDS permita conexiones desde Railway
- Railway IPs: https://docs.railway.app/reference/public-networking#outbound-traffic

### La aplicación se queda "colgada"
- Revisa los logs en Railway
- Verifica que el JAR se esté generando correctamente
- El build puede tardar 3-5 minutos la primera vez

## Configuración de Seguridad AWS RDS

Para que Railway pueda conectarse a tu MySQL en AWS:

1. Ve a AWS Console > RDS > tu instancia MySQL
2. Click en el Security Group
3. Edita Inbound Rules
4. Agrega nueva regla:
   - Type: MySQL/Aurora
   - Port: 3306
   - Source: 0.0.0.0/0 (para desarrollo) o las IPs de Railway
   - Descripción: "Railway access"

**⚠️ Nota de Seguridad:** Para producción, es mejor usar las IPs específicas de Railway o configurar un VPN.

## Comandos Útiles

```bash
# Ver logs en tiempo real
railway logs

# Abrir la app en el navegador
railway open

# Ver variables de entorno
railway variables

# Ejecutar un comando en Railway
railway run ./gradlew build
```

## URLs Importantes

- **Dashboard Railway**: https://railway.app/dashboard
- **Documentación**: https://docs.railway.app
- **Pricing**: Tienes $5 USD gratis cada mes sin tarjeta

## Costos Estimados

- **$5 USD/mes gratis** (suficiente para desarrollo y demos)
- Si excedes, el costo adicional es aproximadamente:
  - $0.000231/GB-hour (RAM)
  - $0.000463/vCPU-hour

Tu aplicación probablemente use ~$3-4 USD/mes, así que estarás dentro del plan gratuito.

## Próximos Pasos Después del Despliegue

1. **Custom Domain**: Conecta tu propio dominio en Settings > Domains
2. **CI/CD**: Railway desplegará automáticamente cada push a main
3. **Monitoreo**: Revisa métricas en el dashboard
4. **Backups**: Configura backups automáticos de la base de datos

---

**¿Necesitas ayuda?** Revisa los logs en Railway o contacta al equipo de desarrollo.