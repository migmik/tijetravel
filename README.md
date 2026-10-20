# Tije Travel — Sistema Web de Agencia de Viajes
**Trabajo Práctico Integrador — Programación II**

Repositorio: [https://github.com/migmik/tijetravel.git](https://github.com/migmik/tijetravel.git)  
Autor: Miguel Pereyra (`miguel.pereyra@alu.inspt.utn.edu.ar`)

---

## 1. Descripción y Propósito del Sistema

Tije Travel es una plataforma web integral para una agencia de viajes argentina, diseñada y construida desde cero bajo una arquitectura desacoplada de dos aplicaciones independientes:
1. **Backend REST API (`tijetravel-backend`)**: Construido con **Java 21**, **Spring Boot 3.3.5**, **Spring Data JPA**, **Spring Security**, **Flyway** y **MySQL**. Provee una API HTTP JSON versionada (`/api/v1/...`) con autenticación HTTP Basic/sesión, validaciones de Bean Validation, control de concurrencia y agregaciones nativas en base de datos.
2. **Frontend Servidor Web (`tijetravel-frontend`)**: Empaquetado como **WAR**, construido con **Spring Boot MVC**, **JSP/JSTL**, **HTML5** y **CSS3**. Renderización 100% del lado del servidor, **sin JavaScript propio** (conforme a las restricciones obligatorias). Consume la API del backend únicamente por HTTP y mantiene su propia sesión de navegación con protección CSRF y prevención de caché.

El código fue escrito con un paradigma imperativo y estructurado simple (bucles tradicionales `for`, condicionales `if-else`, DTOs explícitos), evitando programación funcional compleja (sin streams ni lambdas oscuras), para que un estudiante de segundo año pueda explicar línea por línea el diseño y las decisiones ante el profesor.

---

## 2. Decisiones Arquitectónicas y Justificación de Reglas de Negocio

El profesor exige defender **por qué cada regla reside donde reside**:

| Regla de Negocio / Restricción | Capa donde reside | Justificación Técnica y Académica |
| :--- | :--- | :--- |
| **Integridad referencial y unicidad** (`UNIQUE(nombre, ciudad)` en Hotel, `UNIQUE(turista_id, vuelo_id)` en Reserva) | Base de Datos (DDL Flyway) | Garantiza a nivel motor que jamás existan hoteles homónimos en la misma ciudad ni reservas duplicadas para el mismo turista en el mismo vuelo, incluso ante concurrencia extrema o accesos simultáneos. |
| **No borrar registros con reservas asociadas** | Base de Datos (`ON DELETE RESTRICT`) y Capa de Servicio | Si se elimina un vuelo, hotel o turista con reservas históricas, se corrompe la trazabilidad contable y legal de la agencia. Se valida en servicio y se restringe por Foreign Key. |
| **Normalización de nombres de ciudad** (ignorar tildes, mayúsculas y espacios: ej. "Córdoba" == "cordoba") | `NormalizadorTexto` en Backend | Permite que las comparaciones entre el destino del vuelo y la ciudad del hotel no fallen por errores tipográficos o diferencias de mayúsculas/minúsculas. |
| **Disponibilidad y Ocupación del Hotel** en intervalo `[llegada, partida)` | `HotelService` y `HotelRepository` (Backend) | Las salidas y nuevas entradas el mismo día no se solapan. Se calcula noche por noche verificando que la ocupación simultánea no exceda la capacidad total del hotel. |
| **Cupos de Vuelo por Clase** (Turista vs Primera) | `VueloService` y `ReservaRepository` (Backend) | Se comprueba el número de reservas activas por clase contra el cupo asignado en el vuelo. |
| **Captura Inmutable de Precios** en Reserva | `ReservaService` (Backend) | Al momento de contratar la reserva, se capturan los importes exactos (vuelo ajustado y noche de hotel ajustada). Si luego el catálogo cambia tarifas, las reservas ya contratadas no sufren modificaciones. |
| **Simulación de Pago de 4 Segundos sin JavaScript** | Frontend JSP (`<meta http-equiv="refresh">`) y Backend idempotente | Se utiliza el estándar HTTP de refresco automático del navegador (`<meta http-equiv="refresh" content="4;url=...">`) junto con un spinner animado por CSS. Al cumplirse los 4 segundos, se llama al endpoint de confirmación, el cual es **idempotente** (si la reserva ya fue pagada, no duplica ni cobra recargos). |
| **Alcance Familiar de Clientes** | Backend (`ReservaService` y `TuristaService`) | Un cliente autenticado únicamente puede ver y crear reservas para los integrantes de su propio grupo familiar (`turista.titular_id`). Esta restricción se valida en el servidor, no solo ocultando botones en la vista. |
| **Consultas Puntuales y Agregaciones en BD** | Repositorios Spring Data JPA | Se prohíbe terminantemente hacer `findAll()` y filtrar en memoria. Las búsquedas utilizan queries JPQL con `WHERE`, ordenamiento en base de datos (`ORDER BY`), y el balance contable calcula `COUNT` y `SUM` directamente en el motor MySQL. |
| **Protección contra XSS y CSRF** | Frontend (JSTL `<c:out>` y Spring Security CSRF) | Todo dato dinámico presentado en pantalla se escapa mediante `<c:out value="..."/>` para evitar inyecciones HTML/Script. Todos los formularios POST envían el token CSRF sincronizado. |

---

## 3. Estructura del Proyecto

```
tijetravel/
├── pom.xml                               # POM padre multi-módulo (Java 21, Spring Boot 3.3.5)
├── docker-compose.yml                    # Orquestación local (MySQL 8, Backend API, Frontend WAR)
├── especificacion-integral-para-ia.md    # Especificación funcional y técnica original
├── tijetravel-backend/                   # API REST independiente
│   ├── pom.xml
│   ├── Dockerfile                        # Multi-stage build para Railway / Contenedores
│   └── src/
│       ├── main/
│       │   ├── java/com/tijetravel/backend/
│       │   │   ├── config/               # Spring Security, CORS, Bootstrap Admin
│       │   │   ├── controller/           # Endpoints REST (/api/v1/...)
│       │   │   ├── dto/                  # Contratos de entrada y salida
│       │   │   ├── exception/            # Manejo global de excepciones (400, 401, 403, 404, 409)
│       │   │   ├── model/                # Entidades JPA (Sucursal, Hotel, Vuelo, Turista, Usuario, Reserva)
│       │   │   ├── repository/           # Repositorios JPA con consultas puntuales
│       │   │   ├── service/              # Lógica de negocio y reglas de validación
│       │   │   └── util/                 # Normalizador de texto y comparadores
│       │   └── resources/
│       │       ├── application.properties
│       │       └── db/migration/         # Migraciones Flyway (V1 esquema, V2 seed demo)
│       └── test/                         # Pruebas unitarias e integración con H2 / MockMvc
└── tijetravel-frontend/                  # Servidor Web WAR con JSP / JSTL
    ├── pom.xml                           # Empaquetado <packaging>war</packaging>
    ├── Dockerfile                        # Multi-stage build para despliegue
    └── src/
        ├── main/
        │   ├── java/com/tijetravel/frontend/
        │   │   ├── config/               # Seguridad web, autenticación y sesión
        │   │   ├── controller/           # Controladores Spring MVC (Rutas web)
        │   │   ├── dto/                  # DTOs de vista (sin dependencias de JPA)
        │   │   ├── service/              # Cliente HTTP hacia API Backend (RestClient)
        │   │   └── util/                 # Utilidades de sesión y seguridad
        │   ├── resources/
        │   │   ├── application.properties
        │   │   └── static/css/styles.css # Estilos azul y naranja, diseño responsivo
        │   └── webapp/WEB-INF/jsp/       # Vistas JSP del lado del servidor
        │       ├── comun/                # Encabezado (header) y pie de página (footer)
        │       ├── home.jsp              # Buscador principal con filtros compactos
        │       ├── resultados.jsp        # Paquetes recomendados y sugerencias
        │       ├── vuelos.jsp            # Catálogo de vuelos
        │       ├── hoteles.jsp           # Catálogo con autocomplete datalist HTML sin JS
        │       ├── sucursales.jsp        # Directorio de sucursales
        │       ├── login.jsp             # Inicio de sesión con retorno a Mi Cuenta
        │       ├── registro.jsp          # Autorregistro de nuevo cliente
        │       ├── mi_cuenta.jsp         # Perfil y gestión de grupo familiar
        │       ├── mis_reservas.jsp      # Listado de reservas familiares
        │       ├── reserva_formulario.jsp# Formulario de reserva precargado
        │       ├── pago_procesando.jsp   # Simulación de pago 4 segundos sin JavaScript
        │       ├── reserva_detalle.jsp   # Voucher con desglose contable inmutable
        │       ├── vendedor_reservas.jsp # Gestión y cancelación de reservas pendientes
        │       ├── vendedor_gestion.jsp  # ABM de vuelos, hoteles, sucursales y turistas
        │       ├── admin_usuarios.jsp    # Administración de usuarios y roles
        │       └── admin_balance.jsp     # Balance contable (Vendido vs Potencial)
        └── test/                         # Pruebas de controladores, ruteo y sesión
```

---

## 4. Requisitos y Comandos para Compilar y Ejecutar

### 4.1 Requisitos del Entorno
- **Java 21 LTS**
- **Apache Maven 3.9+**
- **Docker y Docker Compose** (opcional para ejecución en contenedores o MySQL local)

### 4.2 Compilación y Ejecución de Pruebas
Desde la raíz del proyecto:
```bash
# Limpiar y compilar ambos módulos con ejecución de pruebas completas
mvn clean test

# Empaquetar artefactos (JAR backend y WAR frontend)
mvn clean package -DskipTests
```

### 4.3 Ejecución Rápida con Docker Compose (Recomendada)
Para levantar la base MySQL 8 vacía (Flyway inicializa automáticamente el esquema y los datos de prueba), el Backend en el puerto 8080 y el Frontend en el puerto 8081:
```bash
docker compose up --build
```
Una vez levantado:
- **Frontend Web**: [http://localhost:8081](http://localhost:8081)
- **Backend API**: [http://localhost:8080](http://localhost:8080)
- **Base de Datos MySQL**: `localhost:3306` (Base: `tijetravel`, Usuario: `root`, Contraseña: `root`)

### 4.4 Ejecución Local sin Docker (con MySQL en tu máquina)
1. Iniciar tu servidor MySQL local y crear la base vacía:
   ```sql
   CREATE DATABASE tijetravel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Ejecutar el backend:
   ```bash
   mvn spring-boot:run -pl tijetravel-backend
   ```
3. En otra terminal, ejecutar el frontend:
   ```bash
   mvn spring-boot:run -pl tijetravel-frontend
   ```

---

## 5. Cuentas de Acceso de Demostración (Seed Oficial)

| Rol | Usuario | Contraseña | Descripción y Permisos |
| :--- | :--- | :--- | :--- |
| **Visitante** | *(Sin sesión)* | *(N/A)* | Búsqueda de viajes, consulta de vuelos/hoteles/sucursales, autorregistro. |
| **Cliente** | `cliente` | `cliente123` | Titular familiar (DNI 35123456). Acceso a "Mi Cuenta", grupo familiar, reservas propias y pago simulado. |
| **Vendedor** | `vendedor` | `vendedor123` | Gestión de turistas, vuelos, hoteles, sucursales y cancelación de reservas pendientes. |
| **Administrador** | `admin` | `admin123` | Acceso integral: todo lo del vendedor + Gestión de usuarios y Roles + **Balance Contable**. |

---

## 6. Variables de Entorno para Despliegue en Railway

El sistema está preparado para desplegarse como **3 servicios independientes** en Railway sin ningún secreto en Git:

### Servicio 1: MySQL (Plugin oficial de Railway)
Railway genera automáticamente las variables: `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`.

### Servicio 2: Backend API (`tijetravel-backend`)
- `PORT`: Asignado automáticamente por Railway (ej. 8080).
- `SPRING_DATASOURCE_URL`: `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
- `SPRING_DATASOURCE_USERNAME`: `${{MySQL.MYSQLUSER}}`
- `SPRING_DATASOURCE_PASSWORD`: `${{MySQL.MYSQLPASSWORD}}`
- `ADMIN_BOOTSTRAP_USERNAME`: Nombre del administrador inicial temporal (ej. `admin_produccion`).
- `ADMIN_BOOTSTRAP_PASSWORD`: Contraseña segura para el administrador inicial.
- `ADMIN_BOOTSTRAP_DNI`: DNI único del administrador inicial.

### Servicio 3: Frontend WAR (`tijetravel-frontend`)
- `PORT`: Asignado automáticamente por Railway (ej. 8081).
- `BACKEND_API_URL`: URL pública o interna del backend provista por Railway (ej. `https://tijetravel-backend-production.up.railway.app` o `http://backend.railway.internal:8080`).

---

## 7. Recorrido Paso a Paso para la Defensa ante el Profesor

1. **Recorrido del Visitante:**
   - Ingresar a la página principal (`/`).
   - Usar el buscador: Origen **Buenos Aires**, Destino **Córdoba**, Llegada **2026-10-21**, Salida **2026-10-24**, 2 Personas.
   - Observar los **Paquetes Recomendados (Vuelo + Hotel)** con precio orientativo por persona y costo total del grupo.
   - Probar una búsqueda para un día sin vuelos para ver la sugerencia de vuelos alternativos en fechas cercanas.
   - Explorar Hoteles y notar la sugerencia nativa con `<datalist>` al tipear el nombre sin JavaScript.
   - Hacer clic en «Registrarse», completar el formulario con un DNI válido y verificar que la cuenta creada adquiere automáticamente el rol `CLIENTE`.

2. **Recorrido del Cliente:**
   - Iniciar sesión con `cliente` / `cliente123`.
   - El sistema redirige a «Mi Cuenta». Explicar al profesor que si se intenta ingresar a `/login` con la sesión activa, el sistema siempre redirige a «Mi Cuenta» evitando formularios redundantes.
   - En «Mi Cuenta», observar a los integrantes del grupo familiar y agregar un nuevo familiar a cargo.
   - Desde el buscador o resultados, hacer clic en «Reservar» en un paquete.
   - En el formulario de reserva precargado, seleccionar a uno de los integrantes del grupo familiar, clase de vuelo (Turista o Primera) y pensión (Estándar o Completa).
   - Hacer clic en «Confirmar y Proceder al Pago».
   - **Simulación de Pago:** Se presenta la pantalla «Procesando pago seguro...» con una barra de progreso animada por CSS durante exactamente **4 segundos**.
   - Al finalizar los 4 segundos, el navegador redirige a la confirmación: la reserva pasa a estado `PAGADO` con su fecha y hora exacta de pago.
   - Demostrar la **idempotencia**: recargar la página o volver a invocar el enlace no duplica importes ni altera la reserva.

3. **Recorrido del Vendedor:**
   - Iniciar sesión con `vendedor` / `vendedor123`.
   - Ir a «Reservas»: observar el listado global de todas las reservas de la agencia y filtrar por DNI, fechas o estado.
   - Cancelar una reserva que se encuentre en estado `PENDIENTE`.
   - Verificar que las reservas `PAGADAS` tienen bloqueada su modificación o eliminación («Las reservas pagadas no se modifican ni eliminan»).
   - Ir a «Gestión» y mostrar el alta y mantenimiento de vuelos, hoteles y sucursales.

4. **Recorrido del Administrador:**
   - Iniciar sesión con `admin` / `admin123`.
   - Ir a «Usuarios»: consultar el listado paginado, crear nuevos usuarios con roles específicos y modificar el rol de un usuario existente.
   - Ir a «Balance»: mostrar el panel contable. Explicar al profesor que los totales de ventas reales (`PAGADO`) y de potencial de ventas (`PENDIENTE`) son calculados directamente en MySQL mediante consultas agregadas `COUNT` y `SUM` sobre los precios históricos capturados.

