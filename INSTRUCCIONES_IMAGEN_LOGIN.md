# Instrucciones para Agregar Imagen de Fondo al Login

## ✅ Configuración Completada

He modificado el archivo `login.html` para incluir una imagen de fondo con un overlay de gradiente semitransparente.

## 📁 Dónde Colocar la Imagen

Coloca tu imagen de fondo en:
```
src/main/resources/static/images/fondo-login.jpg
```

## 🎨 Opciones de Imagen

### Opción 1: Usar tu Propia Imagen
1. Busca una imagen relacionada con taller mecánico (autos, herramientas, etc.)
2. Renómbrala como `fondo-login.jpg`
3. Cópiala en `src/main/resources/static/images/`

### Opción 2: Usar la Imagen Existente
Si quieres usar la imagen `auto.jpg` que ya tienes:

Cambia esta línea en `login.html` (línea 14):
```css
url('/images/fondo-login.jpg');
```

Por:
```css
url('/images/auto.jpg');
```

### Opción 3: Descargar una Imagen Gratuita
Puedes descargar imágenes gratuitas de:
- **Unsplash**: https://unsplash.com/s/photos/car-repair
- **Pexels**: https://www.pexels.com/search/mechanic/
- **Pixabay**: https://pixabay.com/images/search/car-workshop/

## 🎨 Características del Fondo Actual

El código CSS que agregué incluye:

1. **Imagen de fondo**: Se carga desde `/images/fondo-login.jpg`
2. **Overlay de gradiente**: Un gradiente púrpura semitransparente (85% opacidad) sobre la imagen
3. **Responsive**: Se adapta a diferentes tamaños de pantalla
4. **Fixed**: La imagen permanece fija al hacer scroll (efecto parallax)
5. **Cover**: La imagen cubre toda la pantalla sin deformarse

## 🔧 Personalización del Overlay

Si quieres cambiar el color o transparencia del overlay, edita estas líneas en `login.html`:

```css
/* Actual: Gradiente púrpura con 85% de opacidad */
background-image:
    linear-gradient(135deg, rgba(102, 126, 234, 0.85) 0%, rgba(118, 75, 162, 0.85) 100%),
    url('/images/fondo-login.jpg');
```

### Ejemplos de otros colores:

**Verde oscuro (tema taller mecánico):**
```css
linear-gradient(135deg, rgba(20, 30, 48, 0.8) 0%, rgba(36, 59, 85, 0.8) 100%),
```

**Negro semitransparente (minimalista):**
```css
linear-gradient(135deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.6) 100%),
```

**Verde AutoFixPro (coherente con tu marca):**
```css
linear-gradient(135deg, rgba(33, 165, 47, 0.75) 0%, rgba(128, 205, 124, 0.75) 100%),
```

**Sin overlay (solo imagen):**
```css
background-image: url('/images/fondo-login.jpg');
```

## 📐 Requisitos de la Imagen

Para mejor resultado:
- **Tamaño recomendado**: 1920x1080 px o mayor
- **Formato**: JPG, PNG o WebP
- **Peso**: Menos de 500KB (para carga rápida)
- **Tema**: Relacionado con taller mecánico, autos, herramientas

## 🚀 Aplicar los Cambios

1. Coloca la imagen en la carpeta especificada
2. Ejecuta la aplicación:
   ```bash
   ./gradlew bootRun
   ```
3. Abre el navegador en: `http://localhost:9091/login`
4. Si no ves la imagen, presiona `Ctrl + F5` para limpiar la caché

## 🎯 Resultado Final

Verás:
- ✅ Imagen de fondo cubriendo toda la pantalla
- ✅ Overlay de gradiente púrpura semitransparente
- ✅ Formulario de login centrado con fondo blanco
- ✅ Efecto visual profesional y moderno

## 💡 Sugerencias de Imágenes para Taller Mecánico

**Palabras clave para buscar:**
- "car repair shop"
- "mechanic workshop"
- "auto service center"
- "car maintenance"
- "automotive tools"
- "car engine repair"

**Estilos recomendados:**
- Vista de taller con autos en elevadores
- Herramientas de mecánica organizadas
- Motor de auto de cerca
- Taller mecánico profesional
- Técnicos trabajando

---

**Nota**: El archivo ya está configurado para usar `/images/fondo-login.jpg`. Solo necesitas colocar la imagen en la carpeta correcta.