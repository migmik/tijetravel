# Tije Travel — especificación para construir desde un repositorio vacío

Este documento es autosuficiente. Quien lo reciba **no tendrá acceso al proyecto previo**: debe crear todo el sistema desde cero. Describe resultados y restricciones, sin imponer árbol de carpetas, nombres de clases, tablas concretas ni patrones innecesarios. Puede decidir la organización interna mientras cumpla las reglas y separe responsabilidades.

## Propósito

Construir la web de una agencia de viajes para explorar vuelos, hoteles y sucursales; registrar turistas y usuarios; organizar familias; reservar viajes; simular pagos; y consultar ventas. Es un TP de Programación II. El código debe poder explicarse ante el profesor: separación entre entidades/modelos, acceso a datos, lógica de negocio, controladores y vistas; SOLID aplicado con criterio; excepciones claras; relaciones y persistencia correctas. La aplicación debe funcionar de extremo a extremo y ser accesible desde otra computadora.

## Tecnologías obligatorias

- Java 21, Maven y Spring Boot. Usar versiones compatibles y fijarlas explícitamente.
- Backend independiente con API HTTP JSON: Spring MVC, Spring Data JPA/Hibernate, Bean Validation, Spring Security, MySQL y Flyway. Flyway crea/evoluciona el esquema; Hibernate lo valida en producción. Contraseñas cifradas con un codificador seguro.
- Frontend Java independiente, empaquetado como **WAR**, con Spring MVC, **JSP/JSTL**, HTML y CSS. Renderización del lado del servidor. **Sin JavaScript propio**. Consumir la API por HTTP; el frontend no accede a MySQL ni importa entidades JPA del backend.
- Pruebas automatizadas significativas con JUnit/Spring Test. Se permite H2 en pruebas, además de verificar las migraciones y el recorrido real con MySQL.
- Dos aplicaciones desplegables por separado más MySQL en Railway; Dockerfile para cada aplicación. Puertos configurables por entorno. HTTPS en producción y configuración mediante variables; ningún secreto en Git.

## Datos del dominio

- **Sucursal:** código, ciudad, dirección, teléfono. Se puede buscar por ciudad.
- **Hotel:** código, nombre, ciudad, dirección, teléfono, capacidad total en personas y precio base por persona **por noche**. Nombre + ciudad es único.
- **Vuelo:** número, fecha/hora, origen, destino, asientos totales, cupos por clase turista y primera, precio base por persona. Origen y destino distintos; cupos no negativos y compatibles con el total.
- **Turista:** código, DNI único de 7 u 8 dígitos, nombre, apellido, dirección, email, teléfono fijo y celular, sucursal y titular familiar opcional. Un turista puede existir sin cuenta de usuario. Familiares se vinculan al titular y comparten sucursal.
- **Usuario:** código, nombre de usuario único, contraseña cifrada, rol y DNI de la persona. Roles: `CLIENTE`, `VENDEDOR`, `ADMINISTRADOR`. El cliente está vinculado con un turista. No duplicar personas/DNI al relacionar cuentas. Un nuevo usuario nunca elige privilegios de personal por autorregistro.
- **Reserva:** código, un turista, un vuelo, un hotel, sucursal de contratación conservada como dato histórico, clase de vuelo, tipo de hospedaje, llegada y partida, precio capturado de vuelo y hotel, total, estado pagado/pendiente y fecha de pago. **Una reserva equivale a una persona**, incluso en viajes familiares. Impedir dos reservas del mismo turista para el mismo vuelo.

Mantener integridad referencial y restricciones de unicidad en la base. No borrar registros vinculados a reservas de manera silenciosa. Nuevos turistas siempre requieren DNI.

## Roles y acceso

| Acción | Visitante | Cliente | Vendedor | Administrador |
| --- | --- | --- | --- | --- |
| Buscar viajes, consultar catálogos filtrados, disponibilidad y sucursales | Sí | Sí | Sí | Sí |
| Autorregistrarse como cliente / iniciar sesión | Sí | Sí | Sí | Sí |
| Ver turistas y reservas | No | Solo su grupo familiar | Sí | Sí |
| Crear reserva | No | Solo propia o de su grupo | Sí | Sí |
| Editar/eliminar reserva pendiente | No | No | Sí | Sí |
| Gestionar turistas, vuelos, hoteles y sucursales | No | No | Sí, según permiso | Sí |
| Administrar usuarios/roles y ver balance | No | No | No | Sí |

El backend aplica permisos y alcance familiar en cada petición. Ocultar botones en el frontend no reemplaza esa validación. No permitir que visitantes o clientes obtengan listas globales completas de vuelos, hoteles, turistas, usuarios o reservas. Sí pueden usar búsquedas filtradas autorizadas. Las reservas pagadas no se modifican ni eliminan.

## Sitio y búsqueda

La página inicial tiene un buscador con origen, destino, día de llegada, día de salida del hotel, personas (1 a 20) y orden por precio. Origen/destino son desplegables construidos desde ciudades disponibles. Las ciudades deben ser distintas y la partida posterior a la llegada. El vuelo de ida llega el día indicado como llegada. La búsqueda consulta plazas, pero no crea ni garantiza reservas.

Resultados: primero mostrar **combinaciones vuelo + hotel** con costo orientativo por persona y botón «Reservar»; debajo, vuelos y hoteles separados. Si no hay vuelos para la fecha, sugerir vuelos del destino en otras fechas, aclarando que el alojamiento debe volver a comprobarse. Distinguir claramente sin resultados, parámetros inválidos y error de servicio. El botón lleva al formulario con vuelo, hotel y fechas precargados; quien no inició sesión pasa por login.

Crear páginas de exploración para vuelos, hoteles y sucursales, detalles, formularios de administración, registro/login, «Mi cuenta», turistas/familias, reservas, pago y balance. Filtros compactos, horizontales en escritorio (una o dos líneas), adaptables a móvil. Usar desplegables de valores existentes para origen, destino y ciudad; permitir escribir el nombre del hotel con sugerencias de nombres existentes, sin JavaScript. Menús y acciones visibles según rol. Estilo claro y presentable, azul y naranja, botón «Reservar» naranja, resultados/recomendaciones con leve fondo destacado e imagen de fondo atenuada sin sacrificar legibilidad.

Si una persona ya autenticada visita login, incluso al retroceder en el navegador, debe llegar a «Mi cuenta» y no ver el formulario otra vez. Cerrar sesión invalida el acceso. Formularios y páginas sensibles no deben reaparecer desde caché de forma engañosa.

## Filtros y consultas específicas

- Vuelos: origen, destino, fecha/rango, personas/cupo y orden por precio ascendente/descendente.
- Hoteles: ciudad, nombre, fechas, personas/cupo y orden por precio.
- Sucursales: ciudad seleccionable.
- Turistas: código, DNI y grupo/titular, respetando alcance del cliente.
- Reservas: código de turista, DNI, número de vuelo, código de hotel, rango de llegada y estado pagado/pendiente; filtros combinables y paginación.
- Usuarios: para administrador, código, DNI, nombre de usuario y rol; filtros combinables y paginación.

El profesor exige consultas **puntuales**: no hacer `findAll` y filtrar en memoria cuando el criterio puede resolverse en la base. Filtrar, ordenar, limitar y paginar en SQL/JPA; acotar el grupo familiar en la consulta, no después. Evitar N+1 y una llamada HTTP por cada vuelo/hotel candidato. Usar consultas agregadas para balance. Los listados completos solo se usan cuando verdaderamente se necesitan y están autorizados. Definir índices apropiados, límites de página y conservar los filtros al navegar páginas.

## Disponibilidad, precios y reserva

La disponibilidad de vuelo depende de reservas por clase y su cupo. La del hotel depende del **máximo de ocupación simultánea** en el intervalo `[llegada, partida)`: salida y nueva entrada el mismo día no se solapan. Comprobar otra vez al guardar para impedir sobreventa, también ante solicitudes concurrentes. La fecha de llegada debe coincidir con el día del vuelo; la ciudad del hotel, con el destino del vuelo (normalizar espacios, mayúsculas y acentos para comparar). No permitir partida anterior o igual a llegada.

Precio base de vuelo: **$150.000 a $300.000**, múltiplos de **$10.000**. Precio base de hotel: **$25.000 a $55.000 por persona y noche**, múltiplos de **$1.000**. Se cargan al crear y se pueden editar. Los datos de prueba pueden elegir precios aleatorios dentro de esos rangos, pero **no** recalcularlos al leer. Primera clase suma **10 %** al vuelo; pensión completa suma **5 %** al hotel. El total de una reserva individual es vuelo ajustado + noches × hotel ajustado. Definir redondeo, evitar desbordes y mostrar desglose. Capturar los importes en la reserva: si luego cambia el catálogo, no cambia el precio de reservas ya creadas. Una reserva familiar con varias personas son varias reservas individuales; buscar para N personas comprueba capacidad para N pero no reserva automáticamente a todos.

Al guardar una reserva se registra como pendiente. Un cliente solo reserva para su grupo; vendedor y admin pueden hacerlo según permisos. Validar duplicados, capacidad, coherencia de fechas/ciudades, propiedad y datos inexistentes. Ante errores de red, evitar reintentos ciegos que dupliquen registros.

## Pago y balance

Después de crear una reserva, presentar una pantalla de «procesando pago» durante **4 segundos** mediante una solución HTML sin JavaScript. Es una **simulación académica**: no se ingresan tarjetas ni se cobra dinero real. Al finalizar, marcar la reserva autorizada como pagada, guardar fecha de pago y mostrar el detalle pagado. La confirmación debe ser idempotente y el enlace de continuación protegido para que otro usuario no pague una reserva ajena. Evitar que recargar la página duplique importes.

El administrador ve un balance con cantidades e importes de reservas **pagadas** (total y desglose vuelo/hotel), más reservas **pendientes** e importe potencial separado. No llamar «vendido» a lo pendiente. Calcular mediante agregaciones de base de datos sobre precios históricos capturados.

## Autenticación, API y seguridad

API HTTP JSON versionada, con contratos de entrada/salida distintos de las entidades internas y respuestas de error consistentes. Usar códigos apropiados: 400 validación, 401 no autenticado, 403 prohibido, 404 inexistente y 409 conflicto/cupo/duplicado. Validar en servidor incluso si el navegador valida. Escapar todo dato dinámico en JSP: por ejemplo, un nombre de usuario que contenga `<script>...</script>` debe mostrarse como texto, no ejecutarse. Proteger escrituras con CSRF, renovar ID de sesión al login, invalidar al logout, usar cookies seguras con HTTPS y controlar caché de páginas privadas. No exponer contraseñas ni detalles internos en respuestas.

El frontend puede mantener una sesión propia y reenviar de forma segura la sesión de la API. Documentar la estrategia elegida. Una sesión del backend solo en memoria requiere una instancia o almacenamiento/afinidad adicional si se escala. Para el TP alcanza una instancia backend. Una base vacía necesita un mecanismo **temporal** de alta del primer administrador por variables de entorno; se desactiva tras usarlo. Nunca codificar credenciales en el proyecto.

## Datos de demostración y despliegue

Usar MySQL persistente, backend y frontend como tres servicios Railway. El frontend recibe la URL del backend; el backend recibe URL/usuario/contraseña de MySQL y origen permitido del frontend, todo por variables de entorno. El proceso escucha el puerto asignado. Flyway inicia una base vacía sin SQL manual oculto; el seed de demostración jamás debe cargarse solo en producción. El frontend y la API deben ser accesibles desde otra computadora, sin exponer directamente MySQL.

Para demostrar, se pueden cargar Buenos Aires, Córdoba, Bariloche y Salta, hoteles con nombres/direcciones reales verificadas, vuelos entre estas ciudades y turistas con nombres/apellidos comunes, DNI únicos y algunas familias. Una carga acordada anteriormente fue **15 hoteles, 30 vuelos, 250 turistas y solo 150 cuentas de usuario**; tomarla como escenario de demostración, no como regla fija ni seed obligatorio. Si se desean vuelos diarios hasta una fecha, definir fecha inicial/final y cargar una vez, no generar en cada arranque. No publicar contraseñas de ejemplo.

## Qué significa «terminado»

Las dos aplicaciones compilan, el frontend genera un WAR y las pruebas relevantes pasan. Una MySQL vacía se inicializa con migraciones. Se demuestra navegador → frontend → API → MySQL en local y en tres servicios: visitante busca; cliente se registra, entra, reserva para su grupo, espera cuatro segundos y ve la reserva pagada; vendedor administra pendientes; administrador ve balance pagado y pendiente. Probar también errores de rol y familia, XSS, CSRF, datos inválidos, cupos, duplicados, fechas, concurrencia y cambio de precios de catálogo posterior a una reserva. Los filtros deben ser combinables, paginados y alimentados por datos reales; no provocar lecturas masivas para búsquedas puntuales.

Entregar documentación breve con comandos exactos para compilar, probar, ejecutar localmente y desplegar; variables requeridas **sin valores secretos**; bootstrap de administrador; decisiones importantes y límites reales. Explicar el recorrido completo y poder defender por qué cada regla reside donde reside.

No se requiere pasarela de pago real, emails/SMS, aplicación móvil, JavaScript, microservicios adicionales ni replicar clases del proyecto anterior. Ante una ambigüedad no bloqueante, elegir la opción más simple que satisfaga el objetivo, documentarla y continuar. Una maqueta sin persistencia o endpoints desconectados no cumplen la especificación.
