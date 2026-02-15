## Why

The Dogs application currently has no domain logic or API endpoints — it's a scaffolded Spring Boot project with database connectivity configured but no functionality. We need a full CRUD REST API for managing dogs so that clients can create, retrieve, update, and delete dog records stored in PostgreSQL.

## What Changes

- Add a `Dog` domain entity with properties like name, breed, age, and weight
- Add a Spring Data JDBC repository for persistence
- Add a REST controller exposing CRUD endpoints (`POST`, `GET`, `PUT`, `DELETE`) under `/api/dogs`
- Add a database schema migration for the `dog` table
- Add request/response DTOs and input validation
- Add integration tests using Testcontainers

## Capabilities

### New Capabilities

- `dogs-crud`: Full CRUD operations for dog resources — covers the domain model, persistence layer, REST endpoints, validation, error handling, and database schema

### Modified Capabilities

_(none — this is a greenfield project with no existing capabilities)_

## Impact

- **Code**: New controller, repository, entity, and DTO classes under `br.eti.caiooliveira.dogs`
- **API**: New REST endpoints at `/api/dogs` and `/api/dogs/{id}`
- **Database**: New `dog` table in PostgreSQL
- **Dependencies**: May need `spring-boot-starter-web` added to `pom.xml` (currently only `spring-boot-starter-data-jdbc` is present)
- **Tests**: New integration tests using the existing Testcontainers setup
