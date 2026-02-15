## Context

The Dogs API currently starts with an empty database. For demos and development, having a pre-populated dataset of ~1000 dogs with realistic variety is valuable. The project uses Spring Boot 4.0.2, Spring Data JDBC, and PostgreSQL with schema managed via `schema.sql`.

## Goals / Non-Goals

**Goals:**
- Seed ~1000 dogs with varied breeds, names, ages, and weights on first startup
- Make seeding idempotent — only run when the `dog` table is empty

**Non-Goals:**
- Providing a configurable or toggleable seeding mechanism (always seeds if empty)
- Supporting incremental data updates or partial seeding
- Importing data from external files (data is generated in code)

## Decisions

### Use `ApplicationRunner` for seeding logic
**Decision**: Implement seeding as a Spring `ApplicationRunner` bean.
**Rationale**: `ApplicationRunner` runs after the application context is fully initialized, ensuring the database schema is already created by `schema.sql`. It's the standard Spring Boot lifecycle hook for startup tasks.
**Alternative considered**: `@EventListener(ApplicationReadyEvent)` — functionally similar but `ApplicationRunner` is more conventional for this use case.

### Generate data in code with predefined breed lists
**Decision**: Define arrays of common dog breeds, names, and weight/age ranges in the seeder class. Use randomization to create varied combinations.
**Rationale**: Keeps it self-contained with no external file dependencies. ~30 breeds with realistic weight ranges provides sufficient variety for 1000 dogs.
**Alternative considered**: Load from a CSV or JSON seed file — adds file management overhead for a simple use case.

### Use batch insert via `JdbcClient` for performance
**Decision**: Use Spring's `JdbcClient` with batch operations rather than saving dogs one by one through the repository.
**Rationale**: Inserting 1000 rows individually would be slow. Batch insert is significantly faster and reduces database round-trips.
**Alternative considered**: Using `DogRepository.saveAll()` — Spring Data JDBC `saveAll` still issues individual inserts; `JdbcClient` batch is more efficient.

## Risks / Trade-offs

- [Startup delay on first run] → Batch insert of 1000 rows is fast (~1-2 seconds), acceptable for a one-time operation
- [Randomized data not deterministic] → Acceptable for demo/dev purposes; not used for testing assertions