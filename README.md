# 🎾 CourtMaster API - Backend MVP

CourtMaster es una API REST backend de alto rendimiento diseñada para la gestión integral de reservas de pistas de pádel, administración de clubes, pasarela interna de monedero (saldo) y paneles analíticos para propietarios. 

Desarrollada con **Java 21** y **Spring Boot 4.0.6**, la aplicación implementa mecanismos avanzados de consistencia transaccional y protección contra condiciones de carrera, garantizando un entorno robusto y libre de *overbooking*.

---

## 🛠️ Tecnologías y Stack Técnico

*   **Lenguaje:** Java 21 (LTS)
*   **Framework:** Spring Boot 4.0.6 (Spring Web, Spring Security, Spring Data JPA)
*   **Base de Datos Principal:** PostgreSQL
*   **Caché y Almacenamiento Key-Value:** Redis
*   **Seguridad:** JSON Web Tokens (JWT) & Control de acceso basado en Roles (RBAC)
*   **Infraestructura y Contenedores:** Docker & Docker Compose
*   **Mapeo y Utilidades:** Lombok, Jakarta Validation API
*   **Gestor de Dependencias:** Maven

---

## 🧠 Arquitectura y Diseño de Reglas de Negocio

El núcleo diferenciador de CourtMaster radica en cómo soluciona los problemas críticos de un sistema con alta concurrencia de usuarios en tiempo real.

### 🛡️ 1. Control de Concurrencia Avanzado (Anti-Overbooking)
En sistemas de alta demanda, dos usuarios pueden intentar reservar la misma pista a la misma hora en el mismo milisegundo. Si no se gestiona de forma aislada, ambos pagos podrían procesarse provocando un conflicto.
*   **Solución:** Se ha implementado un **Bloqueo Pesimista de Escritura (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)** en la capa del repositorio al verificar la disponibilidad de las franjas horarias.
*   **Resultado:** El primer hilo en entrar bloquea los registros concurrentes. La segunda petición se encola de forma segura y es rechazada instantáneamente al liberarse el bloqueo, impidiendo duplicidades.

### 💰 2. Consistencia Transaccional (Saldo vs Reserva)
El flujo de negocio exige que un usuario pague la reserva con el dinero de su monedero virtual.
*   **Mecanismo:** El método del servicio está protegido bajo la anotación **`@Transactional`** de Spring.
*   **Resultado:** La operación se ejecuta de manera **atómica**. Si la reserva falla por solapamiento horario o problemas del sistema, se ejecuta un *rollback* completo e inmediato; el saldo del usuario jamás se descuenta si la pista no queda asegurada en el sistema. Al cancelar (`DELETE`), la transacción calcula la devolución en base a las reglas de negocio del club antes de liberar la pista:
    * **Devolución Completa ($100\%$):** Si el usuario cancela con la antelación mínima requerida por el club, el sistema reembolsa la totalidad del importe directamente a su monedero virtual.
    * **Devolución Parcial ($50\%$):** Si la cancelación se realiza fuera de plazo (penalización por cancelación tardía), el sistema libera la pista pero solo reembolsa la mitad del importe, protegiendo los ingresos del club de forma automatizada.

### 🔒 3. Seguridad y Arquitectura DTO Limpia
*   La autenticación está blindada mediante interceptores de **Spring Security** y validación de firmas **JWT**.
*   El controlador de reservas hace uso estricto de estructuras **DTO (Data Transfer Objects)** personalizados (`DashboardReserva`), encargados de mapear la salida. Esto previene la fuga accidental de datos sensibles (como hashes de contraseñas de usuarios o saldos en cuentas) hacia las respuestas JSON públicas.

---

## 🛣️ Catálogo de Endpoints de la API

La URL base de los endpoints protegidos comienza por `/api`. La raíz pública `/` ofrece un estado de salud del sistema.

### 🔐 Autenticación y Registro (`/api/auth`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/registro` | Público | Registra un nuevo usuario público en el sistema. |
| `POST` | `/api/auth/login` | Público | Autentica un usuario y devuelve el token JWT firmado. |

### 🏢 Gestión de Clubes (`/api/clubes`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/clubes` | Público | Lista todos los clubes registrados en la plataforma. |
| `POST` | `/api/clubes` | `ADMIN` | Crea un club y asigna su identificador de propietario (`ownerId`). |
| `PUT` | `/api/clubes/{id}` | `ADMIN`, `OWNER` | Actualiza los datos del club si el usuario es dueño o admin. |

### 🎾 Gestión de Pistas (`/api/pistas`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/pistas` | Autenticado | Lista las pistas. Soporta filtro opcional `?soloActivas=true`. |
| `POST` | `/api/pistas` | `ADMIN`, `OWNER` | Añade una nueva pista al sistema. |
| `PUT` | `/api/pistas/{id}` | `ADMIN`, `OWNER` | Modifica las especificaciones de una pista existente. |
| `DELETE` | `/api/pistas/{id}` | `ADMIN`, `OWNER` | Desactiva de forma lógica una pista del sistema. |

### 📅 Sistema de Reservas (`/api/reservas`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reservas` | Autenticado | Recupera el listado global de reservas. |
| `GET` | `/api/reservas/usuario/{usuarioId}`| Autenticado | Lista el historial de reservas de un usuario concreto. |
| `POST` | `/api/reservas` | Autenticado | Solicita una reserva (Valida saldo, solapamientos y aplica bloqueo). |
| `DELETE` | `/api/reservas/{id}` | Autenticado | Cancela una reserva y reembolsa el saldo correspondiente. |

### 💳 Monedero y Transacciones (`/api/transacciones`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/transacciones/mi-historial` | Autenticado | Recupera el extracto de recargas y gastos del usuario actual. |
| `POST` | `/api/transacciones/recargar` | Autenticado | Recarga saldo en el monedero (`monto`). |

### 📊 Dashboard de Propietario (`/api/dashboard`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/reservas` | `OWNER` | Lista en tiempo real las reservas asociadas al club del dueño. |
| `GET` | `/api/dashboard/ingresos` | `OWNER` | Métrica financiera agregada de ingresos totales del club. |

### 👥 Administración de Usuarios (`/api/usuarios`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/usuarios` | `ADMIN` | Devuelve listado paginado de todos los usuarios registrados. |
| `GET` | `/api/usuarios/perfil` | Autenticado | Devuelve los datos de perfil y saldo del usuario logueado. |
| `PUT` | `/api/usuarios/{id}` | Autenticado | Actualiza los datos personales del usuario. |
| `PATCH` | `/api/usuarios/{id}/ascender`| `ADMIN` | Asciende un usuario al rol de `OWNER`. |
| `DELETE` | `/api/usuarios/{id}` | `ADMIN` | Desactiva a un usuario del sistema por completo. |

---

## ⚙️ Configuración del Entorno (`application.properties`)

El proyecto inyecta de forma segura los secretos mediante variables de entorno del sistema, impidiendo la exposición de credenciales en el código fuente:

```properties
spring.application.name=courtmaster-api

# Configuración Base de Datos (PostgreSQL)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Caché / Distribución (Redis)
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}

# Seguridad (JWT)
courtmaster.jwt.secret=${JWT_SECRET}
courtmaster.jwt.expiration=${JWT_EXPIRATION}
```
## 🐳 Despliegue Automatizado con Docker Compose

El proyecto está completamente contenedorizado y preparado para producción o desarrollo local mediante **Docker** y **Docker Compose**. Esto permite levantar la API junto a sus servicios dependientes (PostgreSQL y Redis) con un único comando, sin necesidad de instalar bases de datos de forma nativa en el sistema host.

### 📄 Archivo `docker-compose.yml` de referencia
El entorno se gestiona mediante el archivo de orquestación que define los tres servicios clave conectados en la misma red interna de Docker:

* **`courtmaster_postgres`**: Contenedor oficial de PostgreSQL encargado de la persistencia de datos mediante volúmenes.
* **`courtmaster_redis`**: Contenedor oficial de Redis para la gestión de cachés y alta disponibilidad de lecturas.
* **`api`**: El contenedor de nuestra aplicación Spring Boot, que empaqueta la lógica de la aplicación y se conecta a los servicios anteriores de manera interna.

### 🏃‍♂️ Pasos para levantar el entorno completo

1. Asegúrate de tener **Docker** y **Docker Desktop** instalados y ejecutándose en tu máquina.
2. Abre la terminal en la raíz del proyecto (donde reside tu archivo `docker-compose.yml`).
3. Ejecuta el comando de construcción y arranque en segundo plano (*detached mode*):
   ```bash
   docker compose up --build -d
   ```
4. **Verificación:** La API se compilará, empaquetará dentro del contenedor y quedará escuchando de forma automática en el puerto `8080`. Puedes tumbar todo el entorno limpiando los volúmenes en cualquier momento con:
   ```bash
   docker compose down -v
   ```
## 🚀 Despliegue y Ejecución en Local

### Prerrequisitos
1. **Java 21 SDK** instalado correctamente.
2. **Docker & Docker Desktop** instalados y en ejecución en el sistema (encargados de levantar automáticamente PostgreSQL y Redis sin instalaciones locales).
---