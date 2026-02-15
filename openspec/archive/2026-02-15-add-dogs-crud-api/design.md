## Context

This is a greenfield Spring Boot 4.0.2 application (Java 25) with Spring Data JDBC and PostgreSQL already configured. Docker Compose and Testcontainers are set up for local development and testing. The project has no domain code yet — only the application bootstrap class exists. We need to build the first CRUD API for the `Dog` resource.

## Goals / Non-Goals

**Goals:**
- Expose a RESTful CRUD API for dogs at `/api/dogs`
- Persist dog records in PostgreSQL using Spring Data JDBC
- Validate input on create and update operations
- Return appropriate HTTP status codes and error responses
- Cover the API with integration tests using Testcontainers

**Non-Goals:**
- Authentication or authorization (can be added later)
- Pagination, sorting, or filtering on the list endpoint (keep it simple for now)
- API versioning
- Caching
- Soft deletes — deletes are permanent

## Decisions

### 1. Use Spring Data JDBC (not JPA)

The project already includes `spring-boot-starter-data-jdbc`. Spring Data JDBC is simpler than JPA — no lazy loading, no dirty checking, no session cache. It maps well to a straightforward CRUD use case with a single aggregate root. No reason to switch.

### 2. Use Java records for DTOs

Java records provide immutable, concise data carriers. We'll use separate request and response records to decouple the API contract from the persistence entity. The `Dog` entity itself will be a class annotated with Spring Data JDBC annotations.

**Alternatives considered:** Using the entity directly as the request/response model — rejected because it couples the API to the persistence layer and makes validation harder.

### 3. Use `spring-boot-starter-web` (servlet-based)

Add `spring-boot-starter-web` to the project. The reactive stack (`spring-boot-starter-webflux`) is unnecessary for this use case — we have a blocking JDBC data source and no need for reactive streams.

### 4. Schema management with `schema.sql`

Use Spring Boot's built-in `schema.sql` initialization for the database schema. This is sufficient for a single-table greenfield project. Flyway or Liquibase can be introduced later if migration complexity grows.

**Alternatives considered:** Flyway — overkill for a single table with no existing data to migrate.

### 5. REST endpoint design

| Operation   | Method   | Path             | Status  |
|-------------|----------|------------------|---------|
| Create      | POST     | `/api/dogs`      | 201     |
| List all    | GET      | `/api/dogs`      | 200     |
| Get by ID   | GET      | `/api/dogs/{id}` | 200     |
| Update      | PUT      | `/api/dogs/{id}` | 200     |
| Delete      | DELETE   | `/api/dogs/{id}` | 204     |

404 is returned when a dog is not found by ID. 400 for validation errors.

### 6. Dog entity fields

| Field  | Type    | Constraints          |
|--------|---------|----------------------|
| id     | Long    | Auto-generated (PK)  |
| name   | String  | Required, max 100    |
| breed  | String  | Required, max 100    |
| age    | Integer | Required, >= 0       |
| weight | Double  | Required, > 0        |

### 7. Package structure

All classes under `br.eti.caiooliveira.dogs`:
- `Dog` — entity
- `DogRepository` — Spring Data JDBC repository interface
- `DogController` — REST controller
- `DogRequest` — input record (create/update)
- `DogResponse` — output record

Flat package — no sub-packages needed for a single aggregate.

## Risks / Trade-offs

- **No pagination on list endpoint** → Could be slow with large datasets. Acceptable for now; pagination can be added as a separate change.
- **`schema.sql` for DDL** → No versioned migrations. If the schema evolves, we'll need to introduce Flyway. Low risk since this is the first table.
- **No API versioning** → Breaking changes would affect all clients. Acceptable for an initial API with no consumers yet.
