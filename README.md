# Eventia Core API

Sistema de gestión de eventos, participantes y registros de asistencia desarrollado con Spring Boot siguiendo los principios de Clean Architecture.

## Tabla de Contenidos

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Ejecución](#ejecución)
- [Pruebas](#pruebas)
- [API Documentation](#api-documentation)
- [Pipeline CI/CD](#pipeline-cicd)
- [Docker](#docker)
- [Estructura del Proyecto](#estructura-del-proyecto)

## Descripción

**Eventia Core API** es un backend REST para la gestión integral de eventos. Permite:

- Crear, actualizar, consultar y eliminar eventos
- Gestionar participantes con validación de datos únicos
- Registrar asistencia con control de capacidad y validaciones de negocio
- Realizar check-in de participantes
- Generar estadísticas de eventos en tiempo real
- Caché de consultas frecuentes con Redis

## Arquitectura

El proyecto está desarrollado siguiendo **Clean Architecture**, separando las responsabilidades en capas:

### Capas de la Aplicación

```
┌─────────────────────────────────────────┐
│          API Layer (Controllers)        │
│  - REST Controllers                     │
│  - Exception Handlers                   │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Application Layer (DTOs)           │
│  - Request/Response DTOs                │
│  - Mappers                              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Domain Layer (Business Logic)      │
│  - Entities                             │
│  - Services                             │
│  - Repository Interfaces                │
│  - Business Rules                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│    Infrastructure Layer (External)      │
│  - JPA Repositories                     │
│  - Database Models                      │
│  - Cache Implementation (Redis)         │
│  - External Services                    │
└─────────────────────────────────────────┘
```

### Ventajas de Clean Architecture

1. **Independencia de frameworks**: La lógica de negocio no depende de Spring Boot
2. **Testeable**: Fácil de probar cada capa de forma independiente
3. **Independencia de UI**: Puede usarse con cualquier interfaz
4. **Independencia de base de datos**: Puede cambiar PostgreSQL por otra DB
5. **Mantenible**: Código organizado y fácil de entender

## Tecnologías

### Backend
- **Java 21**: Lenguaje de programación
- **Spring Boot 3.5.7**: Framework principal
- **Spring Data JPA**: Persistencia de datos
- **PostgreSQL**: Base de datos relacional
- **Redis**: Sistema de caché
- **Lombok**: Reducción de código boilerplate

### Pruebas
- **JUnit 5**: Framework de pruebas
- **Mockito**: Mocking para pruebas unitarias
- **AssertJ**: Assertions fluidas
- **H2**: Base de datos en memoria para tests
- **MockMvc**: Pruebas de controllers

### Análisis Estático y Calidad
- **SpotBugs**: Detección de bugs
- **PMD**: Análisis de código
- **Checkstyle**: Estilo de código
- **JaCoCo**: Cobertura de pruebas

### Documentación
- **SpringDoc OpenAPI**: Documentación automática de API

### DevOps
- **Docker**: Contenedores
- **Docker Compose**: Orquestación de servicios
- **GitHub Actions**: CI/CD

## Requisitos

### Ejecución Local
- Java 21 o superior
- PostgreSQL 15 o superior
- Redis 7 o superior
- Gradle 8.5 o superior (incluido con wrapper)

### Ejecución con Docker
- Docker 20.10 o superior
- Docker Compose 2.0 o superior

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/event-core.git
cd event-core
```

### 2. Configurar variables de entorno

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

Editar `.env` con tus configuraciones:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=eventcore
DB_USER=postgres
DB_PASSWORD=tu_password

REDIS_HOST=localhost
REDIS_PORT=6379
```

### 3. Crear base de datos

```sql
CREATE DATABASE eventcore;
```

## Ejecución

### Opción 1: Ejecución Local

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar la aplicación
./gradlew bootRun
```

La API estará disponible en: `http://localhost:8080/api`

### Opción 2: Ejecución con Docker Compose (Recomendado)

```bash
# Construir y levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

Servicios disponibles:
- **API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379

## Pruebas

### Ejecutar todas las pruebas

```bash
./gradlew test
```

### Ejecutar por tipo

```bash
# Pruebas unitarias
./gradlew test --tests "*Test"

# Pruebas de integración
./gradlew test --tests "*IntegrationTest"

# Pruebas E2E
./gradlew test --tests "*E2ETest"
```

### Generar reporte de cobertura

```bash
./gradlew jacocoTestReport
```

El reporte HTML estará en: `build/reports/jacoco/test/html/index.html`

### Análisis estático

```bash
# Ejecutar todos los análisis
./gradlew securityCheck

# Ejecutar individualmente
./gradlew spotbugsMain
./gradlew pmdMain
./gradlew checkstyleMain
```

## API Documentation

### Swagger UI

Una vez la aplicación esté ejecutándose, acceder a:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

### Endpoints Principales

#### Events

```http
POST   /api/events                    # Crear evento
GET    /api/events                    # Listar todos los eventos
GET    /api/events/{id}               # Obtener evento por ID
GET    /api/events/upcoming           # Eventos próximos
GET    /api/events/status/{status}    # Eventos por estado
PUT    /api/events/{id}               # Actualizar evento
PATCH  /api/events/{id}/cancel        # Cancelar evento
DELETE /api/events/{id}               # Eliminar evento
```

#### Participants

```http
POST   /api/participants              # Crear participante
GET    /api/participants              # Listar participantes
GET    /api/participants/{id}         # Obtener participante por ID
GET    /api/participants/email/{email} # Buscar por email
PUT    /api/participants/{id}         # Actualizar participante
DELETE /api/participants/{id}         # Eliminar participante
```

#### Attendances

```http
POST   /api/attendances                        # Registrar asistencia
GET    /api/attendances/{id}                   # Obtener asistencia
GET    /api/attendances/event/{eventId}        # Asistencias de evento
GET    /api/attendances/participant/{id}       # Asistencias de participante
GET    /api/attendances/event/{id}/statistics  # Estadísticas del evento
PATCH  /api/attendances/{id}/check-in          # Hacer check-in
PATCH  /api/attendances/{id}/cancel            # Cancelar asistencia
```

### Ejemplo de Uso

```bash
# Crear un evento
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Conferencia Tech 2025",
    "description": "Conferencia anual de tecnología",
    "location": "Centro de Convenciones",
    "startDate": "2025-12-01T09:00:00",
    "endDate": "2025-12-01T18:00:00",
    "capacity": 500
  }'
```

## Pipeline CI/CD

El proyecto incluye un pipeline completo de GitHub Actions que se ejecuta en cada push o pull request:

### Etapas del Pipeline

1. **Instalación de dependencias**
   ```bash
   ./gradlew build -x test
   ```

2. **Pruebas unitarias**
   ```bash
   ./gradlew test
   ```

3. **Pruebas de integración**
   - Levanta servicios de PostgreSQL y Redis
   - Ejecuta pruebas contra servicios reales

4. **Pruebas E2E**
   - Pruebas end-to-end con MockMvc

5. **Análisis estático**
   - SpotBugs
   - PMD
   - Checkstyle

6. **Generación de reportes**
   - Reportes de pruebas
   - Cobertura de código con JaCoCo

### Estado del Pipeline

Si todas las etapas pasan exitosamente, se imprime **OK** en consola.

## Docker

### Dockerfile

El proyecto usa **multi-stage build** para optimizar el tamaño de la imagen:

1. **Build stage**: Compila la aplicación con Gradle
2. **Runtime stage**: Ejecuta con JRE Alpine (imagen ligera)

### Docker Compose

Servicios incluidos:

- **postgres**: Base de datos PostgreSQL 15
- **redis**: Caché Redis 7
- **app**: Aplicación Spring Boot

Todos los servicios incluyen:
- Health checks
- Restart policies
- Volúmenes persistentes
- Red compartida

## Estructura del Proyecto

```
event-core/
├── src/
│   ├── main/
│   │   ├── java/cue/edu/co/eventcore/
│   │   │   ├── api/                      # Capa API
│   │   │   │   ├── controllers/          # REST Controllers
│   │   │   │   └── exceptions/           # Exception handlers
│   │   │   ├── application/              # Capa de Aplicación
│   │   │   │   ├── dtos/                 # DTOs Request/Response
│   │   │   │   └── mappers/              # Mappers
│   │   │   ├── domain/                   # Capa de Dominio
│   │   │   │   ├── entities/             # Entidades de negocio
│   │   │   │   ├── repositories/         # Interfaces de repositorios
│   │   │   │   ├── services/             # Servicios de dominio
│   │   │   │   └── exceptions/           # Excepciones de dominio
│   │   │   └── infrastructure/           # Capa de Infraestructura
│   │   │       ├── persistence/          # Persistencia
│   │   │       │   ├── models/           # Entidades JPA
│   │   │       │   ├── jpa/              # Spring Data repositories
│   │   │       │   ├── mappers/          # Mappers JPA
│   │   │       │   └── repositories/     # Implementaciones
│   │   │       ├── cache/                # Caché Redis
│   │   │       └── config/               # Configuraciones
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/cue/edu/co/eventcore/
│       │   ├── domain/services/          # Tests unitarios
│       │   ├── integration/              # Tests de integración
│       │   └── e2e/                      # Tests E2E
│       └── resources/
│           └── application-test.properties
├── config/                               # Configuración análisis estático
│   ├── checkstyle/
│   └── pmd/
├── .github/workflows/                    # GitHub Actions
├── docker-compose.yml
├── Dockerfile
├── build.gradle
└── README.md
```

## Justificación de Tecnologías

### Java 21 + Spring Boot
- Ecosistema maduro y robusto
- Amplia comunidad y documentación
- Excelente para aplicaciones empresariales
- Spring Boot simplifica la configuración

### PostgreSQL
- Base de datos relacional robusta
- Soporta transacciones ACID
- Excelente rendimiento
- Open source

### Redis
- Caché en memoria ultra-rápido
- Reduce latencia en consultas frecuentes
- Mejora rendimiento de estadísticas
- Fácil integración con Spring

### Clean Architecture
- Separación de responsabilidades
- Código testeable
- Independencia de frameworks
- Fácil mantenimiento
- Escalable

### Docker
- Entorno consistente
- Fácil despliegue
- Aislamiento de servicios
- Portabilidad

## Autores

- Universidad CUE - Proyecto Final

## Licencia

Este proyecto es de código abierto bajo la licencia MIT.
