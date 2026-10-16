# Lovra

![Java](https://img.shields.io/badge/Java-25-blue)
![Quarkus](https://img.shields.io/badge/Quarkus-3.39.4-blue)
![Status](https://img.shields.io/badge/status-under%20development-orange)
![License](https://img.shields.io/badge/license-MIT-green)

## About Lovra

Lovra is an evolving platform for managing 3D printing operations and related workflows. It began as a practical solution to real operational needs and is being developed with an emphasis on maintainability, sound domain modeling, and continuous improvement.

This repository contains the **backend API**. The project is under active development: its domain model, business rules, and architecture are intentionally subject to change as requirements emerge. The current implementation is a foundation, not a finalized product or a fixed definition of its future scope.

## Tech Stack

- **Java 25** and **Quarkus 3.39.4**
- **Maven** for builds and dependency management
- **PostgreSQL**, **Hibernate ORM**, and **Panache** for persistence
- **MapStruct** and **Jakarta Bean Validation** for mapping and validation
- **Keycloak** and **OpenID Connect** for authentication
- **SmallRye OpenAPI**, **SmallRye Health**, and **Micrometer/Prometheus** for API documentation and operational visibility
- **JUnit**, **Mockito**, and Quarkus testing tools
- **Docker Compose** for local identity infrastructure

## Architecture

The backend follows a layered approach, separating REST resources, application services, persistence repositories, domain entities, and API contracts (DTOs). MapStruct handles entity/DTO mapping, while domain-specific exceptions provide structured error handling.

The architecture and persistence model are evolving. Database migration tooling is included in the project, but versioned migrations are not yet part of the current development workflow.

## Getting Started

### Prerequisites

- JDK 25
- Maven
- PostgreSQL for the application database
- Docker with Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/JoaoGabrielCarvalhoL/lovra.git
cd lovra
```

### 2. Configure local environment

The application expects these environment variables:

| Variable | Purpose |
| --- | --- |
| `POSTGRES_LOCAL_URL` | JDBC URL for the application database |
| `POSTGRES_LOCAL_USERNAME` | Application database username |
| `POSTGRES_LOCAL_PASSWORD` | Application database password |
| `OIDC_AUTH_SERVER` | Keycloak realm URL |
| `OIDC_CLIENT_ID` | OIDC client identifier |

The Docker Compose services require `KEYCLOAK_DB_NAME`, `KEYCLOAK_DB_USER`, `KEYCLOAK_DB_PASSWORD`, `KEYCLOAK_ADMIN_USER`, and `KEYCLOAK_ADMIN_PASSWORD`. Configure these locally (for example, with a non-committed Compose `.env` file). Never commit credentials.

The application PostgreSQL database is **not** provisioned by this Compose file; create and configure it separately. Keycloak imports realm configuration from `./keycloak/realms`.

### 3. Start Keycloak

```bash
docker compose up -d
```

Keycloak is exposed at `http://localhost:8081` in the current local setup.

### 4. Run the API

```bash
mvn quarkus:dev
```

The API runs at `http://localhost:8080` by default.

> **Development data warning:** The development profile currently uses Hibernate's `drop-and-create` schema strategy. Starting the application may delete and recreate application tables. Use a disposable development database; never point this profile at data you need to retain.

## API Documentation & Testing

With the API running in development mode:

- **Swagger UI:** `http://localhost:8080/q/swagger-ui`
- **OpenAPI specification:** `http://localhost:8080/q/openapi`

Protected endpoints require a valid access token issued by the configured Keycloak realm. Refer to the generated OpenAPI specification for current routes, request schemas, and responses.

Run the unit tests:

```bash
mvn clean test
```

Run the Maven verification lifecycle:

```bash
mvn clean verify
```

Integration testing with isolated infrastructure (including Testcontainers) is planned for a later stage. The commands above do not imply that an integration test suite is already in place.

## License

Lovra is licensed under the [MIT License](LICENSE).
