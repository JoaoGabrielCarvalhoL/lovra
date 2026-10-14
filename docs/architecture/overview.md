
# Lovra — Architecture Overview

## 1. Purpose

Lovra is a business management application designed to support a small-scale 3D printing operation.

The application aims to centralize information currently managed through spreadsheets, messaging applications, and informal processes.

Its primary business objectives are:

- Organize printable models and validated products.
- Maintain information about filaments and purchased spools.
- Support product cost estimation and pricing.
- Manage customers, quotations, and orders.
- Preserve historical commercial information.
- Reduce manual work and inconsistencies in pricing.

Lovra is also an engineering project intended to support the study and practical application of software architecture, domain modeling, backend development, frontend development, and infrastructure.

## 2. Project Scope

### 2.1 Current Scope

The application is initially designed for a single small business operation.

The current scope includes:

- Authentication through an external identity provider.
- Management of digital 3D printing models.
- Management of validated physical products.
- Management of filament definitions and purchased spools.
- Management of product materials and photographs.
- Management of customers and their contact information.

The following capabilities are planned but have not yet been fully modeled or implemented:

- Production cost calculation.
- Product pricing.
- Customer quotations.
- Sales orders.
- Generation of quotation documents.
- Commercial reporting.

### 2.2 Out of Scope

The following capabilities are not part of the initial implementation:

- Multi-tenant architecture.
- Marketplace integration.
- Payment processing.
- Automated WhatsApp messaging.
- Shipping and logistics management.
- Advanced inventory management.
- Automated printer integration.
- Manufacturing execution and print-job monitoring.

These capabilities may be reconsidered if future business requirements justify them.

## 3. Technology Stack

### 3.1 Backend

- Java
- Quarkus
- Jakarta Persistence
- Hibernate ORM
- PostgreSQL
- REST APIs
- Maven

The backend is responsible for business rules, persistence, validation, authorization, and integration with infrastructure services.

### 3.2 Frontend

- Vue 3
- TypeScript
- Vite
- PrimeVue 4
- PrimeIcons
- Pinia
- Vue Router

The frontend is implemented as a Single Page Application (SPA).

PrimeVue is the primary component library. Existing components should be preferred over custom implementations when they satisfy the application requirements.

Bootstrap is installed but is not currently required by the application layout.

### 3.3 Authentication

- Keycloak
- OpenID Connect (OIDC)
- OAuth 2.0 Authorization Code Flow with PKCE

Keycloak is responsible for user authentication and identity management.

The Vue application authenticates users through Keycloak and obtains access tokens.

The Quarkus backend validates access tokens and protects authenticated endpoints.

Business entities are managed by Lovra and are not stored in Keycloak.

### 3.4 Infrastructure

The development environment currently includes:

- PostgreSQL for Lovra application data.
- PostgreSQL for Keycloak data.
- Keycloak running through Docker Compose.
- Backend and frontend running locally during development.

The application PostgreSQL instance is managed separately from the Keycloak Docker Compose services.

## 4. High-Level Architecture

Lovra follows a client-server architecture.

```text
+-----------------------------+
|          Vue SPA            |
|                             |
| Vue 3 + TypeScript          |
| PrimeVue + Pinia + Router   |
+--------------+--------------+
               |
               | HTTP / REST
               | Bearer Access Token
               v
+-----------------------------+
|       Quarkus Backend       |
|                             |
| REST Resources              |
| Application Services        |
| Domain Entities             |
| Persistence                 |
+----------+------------------+
           |
           +----------------------+
           |                      |
           v                      v
+-------------------+   +-------------------+
|    PostgreSQL     |   |    Filesystem     |
|                   |   |                   |
| Business Data     |   | 3D Model Files    |
| Product Photos    |   |                   |
+-------------------+   +-------------------+

+-----------------------------+
|          Keycloak           |
|                             |
| Authentication              |
| Identity Management         |
| Token Issuance              |
+-----------------------------+
```

Keycloak is an external identity provider used by the frontend and backend authentication flow.

The filesystem is used for preserving digital 3D printing files, while PostgreSQL stores business data and product photographs.

## 5. Backend Organization

The backend currently uses Jakarta Persistence entities to represent the main business concepts.

The intended application structure separates responsibilities between:

- REST resources: HTTP endpoints and request/response handling.
- Application services: use-case orchestration and business operations.
- Domain entities: business state, relationships, and invariants.
- Repositories: persistence operations and queries.
- Infrastructure components: filesystem access and external integrations.

This organization describes the intended separation of responsibilities and does not imply that every layer or service has already been implemented.

Business behavior should be introduced as requirements are implemented, rather than generating generic CRUD services for every entity without considering their responsibilities.

## 6. Domain Overview

### 6.1 PrintModel

Represents a digital 3D printing model selected for preservation and potential use by Lovra.

A PrintModel is not necessarily a validated or commercially available product.

Its validation status may be:

- PENDING_VALIDATION
- VALIDATED
- REJECTED

A model is validated when a successful physical print is evaluated as suitable.

Operational printing failures do not automatically imply that the digital model should be rejected.

### 6.2 StoredFile

Represents metadata and the logical storage reference of a digital file preserved outside the database.

The actual binary content is stored in the filesystem.

StoredFile is associated with PrintModel and is not responsible for managing physical file deletion through JPA cascading operations.

### 6.3 Product

Represents a physical item that has been printed and validated, with information required for future reproduction and commercialization.

A Product references its originating PrintModel.

A Product may contain multiple material definitions and photographs.

### 6.4 ProductMaterial

Represents the composition of a Product.

It associates a Product with a Filament and records the required material weight in grams.

ProductMaterial references Filament rather than FilamentSpool because it describes the material required for production, not the physical spool used during a specific printing operation.

### 6.5 Filament

Represents a reusable material definition, including its name, brand, color, and material type.

A Filament is independent of individual purchases.

### 6.6 FilamentSpool

Represents a purchased physical spool associated with a Filament.

It records acquisition information such as purchase price, initial weight, and purchase date.

The costing policy used when multiple spools of the same filament exist remains to be defined.

### 6.7 ProductPhoto

Represents a photograph of a validated physical product.

ProductPhoto stores image metadata and binary content in PostgreSQL.

Photographs are associated with a Product and have an explicit display order.

### 6.8 Customer

Represents a person with whom Lovra maintains a commercial relationship.

A Customer may exist before completing a purchase.

Customer information includes identification, optional notes, and associated contact methods.

### 6.9 CustomerContact

Represents a communication channel associated with a Customer.

Initially supported contact types are:

- WHATSAPP
- PHONE
- EMAIL

A Customer may have multiple contact records.

## 7. Data Storage Strategy

Lovra currently uses two different binary storage strategies.

### 7.1 Digital Printing Models

Digital 3D printing files are stored in the filesystem.

Their metadata and storage references are maintained in PostgreSQL.

The filesystem storage root must be externally configurable to support different execution environments.

Database records should store logical or relative storage references rather than environment-specific absolute paths.

### 7.2 Product Photographs

Product photographs are stored directly in PostgreSQL using the BYTEA data type.

The current design keeps binary content within ProductPhoto rather than introducing a separate binary-content entity.

Photo listing operations should avoid retrieving binary content when only metadata is required.

Maximum file size, accepted image formats, and maximum photographs per product remain to be defined.

## 8. Security

Authentication is delegated to Keycloak.

The frontend uses Authorization Code Flow with PKCE.

The backend acts as a protected resource server and validates access tokens.

The application currently distinguishes between authentication and business authorization.

Detailed role-based access control must be introduced according to concrete application requirements.

Authorization rules must be enforced by the backend and must not depend exclusively on frontend navigation or component visibility.

## 9. Development Principles

The project follows these principles:

1. Model business concepts according to actual requirements.
2. Avoid introducing abstractions solely for hypothetical future scenarios.
3. Separate business rules from infrastructure-specific concerns.
4. Preserve historical commercial information when business values change over time.
5. Prefer explicit domain operations over unrestricted state mutation when implementing business behavior.
6. Use existing frontend components when appropriate instead of recreating common UI elements.
7. Introduce architectural complexity when its benefits justify its maintenance cost.
8. Document significant architectural decisions and their consequences.
9. Keep implementation, documentation, and business understanding aligned.

## 10. Architectural Decision Records

Significant architectural decisions are documented in the `docs/adr` directory.

Each ADR should describe:

- Context
- Decision
- Alternatives considered
- Consequences
- Status

Decisions that have not yet been finalized must be identified as pending rather than presented as accepted.

## 11. Open Questions

The following subjects require further analysis:

- Material costing policy.
- Product pricing and profit margin calculation.
- Historical pricing snapshots.
- Quotation and order domain models.
- Customer and product lifecycle rules.
- Product photograph upload restrictions.
- Filesystem consistency and file deletion workflows.
- Inventory tracking and spool consumption.
- Reporting requirements.
- Future support for multiple business operators or tenants.

These subjects should be resolved incrementally as their respective use cases are designed.

## 12. Document Maintenance

This document provides a high-level architectural view of Lovra.

Detailed domain relationships are documented in `domain-model.md`.

Pricing concepts and policies are documented in `pricing.md`.

Specific architectural decisions are recorded separately as ADRs.

The documentation must evolve alongside the implementation and should distinguish implemented behavior, accepted design decisions, and future plans.
