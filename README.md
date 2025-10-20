# AutoFixPro - Sistema de Gestión de Taller Mecánico

AutoFixPro es una aplicación web diseñada para la gestión integral de un taller mecánico. Permite administrar clientes, vehículos, órdenes de servicio y notificaciones, proporcionando una solución completa desde el registro del cliente hasta la entrega del vehículo reparado.

## Arquitectura y Patrones de Diseño

El proyecto sigue una arquitectura en capas, desacoplando la presentación, la lógica de negocio y el acceso a datos. Se han implementado varios patrones de diseño para asegurar un código mantenible, escalable y robusto.

### 1. Arquitectura Model-View-Controller (MVC)

La aplicación utiliza **Spring MVC** para separar las responsabilidades:

-   **Modelo (Model):** Representado por las clases de entidad (`Cliente`, `Vehiculo`, `OrdenServicio`, etc.) que definen la estructura de los datos.
-   **Vista (View):** Implementada con **Thymeleaf**. Son las plantillas HTML (`dashboard.html`, `login.html`, etc.) que renderizan la interfaz de usuario.
-   **Controlador (Controller):** Clases anotadas con `@Controller` (`WebController`, `AuthController`) que manejan las solicitudes del usuario, procesan la entrada y devuelven la vista apropiada.

### 2. API RESTful

Se expone una API REST para operaciones de backend y para permitir la integración con otros sistemas.

-   **Controladores REST:** Clases anotadas con `@RestController` (`ClienteController`, `VehiculoController`) que manejan solicitudes HTTP y devuelven datos en formato JSON.
-   **BaseController:** Se utiliza un controlador base (`BaseController`) para estandarizar las respuestas de la API, implementando un patrón similar a un **Factory Method** para crear respuestas consistentes de éxito y error.

### 3. Patrón de Capa de Servicio (Service Layer)

La lógica de negocio está encapsulada en clases de servicio (`ClienteService`, `VehiculoService`, `OrdenServicioService`).

-   **Separación de responsabilidades:** Los controladores delegan toda la lógica de negocio a los servicios. Esto mantiene a los controladores ligeros y centrados en manejar la solicitud/respuesta HTTP.
-   **Transaccionalidad:** Los servicios son el lugar ideal para gestionar las transacciones de la base de datos (por ejemplo, usando `@Transactional`).

### 4. Patrón Repositorio (Repository Pattern)

El acceso a los datos se gestiona a través de interfaces de repositorio (probablemente usando **Spring Data JPA**).

-   **Abstracción de datos:** Los repositorios (`ClienteRepository`, `VehiculoRepository`, etc.) abstraen la lógica de acceso a datos, permitiendo cambiar la fuente de datos subyacente con un impacto mínimo en el resto de la aplicación.

### 5. Inyección de Dependencias (Dependency Injection - DI)

El framework **Spring** gestiona el ciclo de vida de los objetos y sus dependencias.

-   **Inversión de Control (IoC):** En lugar de que los componentes creen sus propias dependencias, el contenedor de Spring las "inyecta" (usando `@Autowired`). Esto reduce el acoplamiento y facilita las pruebas unitarias.

### 6. Patrón Fachada (Facade Pattern)

Se utiliza para proporcionar una interfaz simplificada a un subsistema más complejo.

-   **`NotificacionServicio`:** Actúa como una fachada para el sistema de notificaciones. Simplifica el envío de notificaciones (por ejemplo, a través de AWS SNS) desde la lógica de negocio, ocultando los detalles de implementación del cliente.

## Clases Principales

### Controladores

-   `AuthController`: Gestiona la autenticación (login, registro) y el perfil del usuario.
-   `WebController`: Maneja la renderización de las páginas web principales (dashboard, detalles de cliente/vehículo).
-   `ClienteController`: API REST para las operaciones CRUD de clientes.
-   `VehiculoController`: API REST para registrar y consultar vehículos.
-   `OrdenServicioController`: API REST para gestionar las órdenes de servicio.
-   `NotificationController` y `SnsController`: Endpoints para enviar y gestionar notificaciones a través de servicios como AWS SNS.
-   `HealthController`: Proporciona endpoints (`/status`, `/health`) para el monitoreo de la salud de la aplicación, esencial para despliegues en la nube.

### Servicios

-   `ClienteService`: Lógica de negocio para la gestión de clientes.
-   `VehiculoService`: Lógica de negocio para la gestión de vehículos.
-   `OrdenServicioService`: Lógica de negocio para las órdenes de servicio.
-   `UsuarioService`: Gestiona el registro y la autenticación de usuarios.
-   `NotificacionServicio` y `AwsSnsService`: Encapsulan la lógica para enviar notificaciones por SMS o email.

### Entidades

-   `Cliente`: Representa a un cliente del taller.
-   `Vehiculo`: Representa un vehículo asociado a un cliente.
-
 `OrdenServicio`: Representa una orden de trabajo para un vehículo.
-   `Usuario`: Representa a un usuario del sistema con roles de seguridad.
