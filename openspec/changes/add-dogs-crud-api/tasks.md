## 1. Project Setup

- [x] 1.1 Add `spring-boot-starter-web` and `spring-boot-starter-validation` dependencies to `pom.xml`
- [x] 1.2 Create `src/main/resources/schema.sql` with the `dog` table DDL (id serial primary key, name varchar(100) not null, breed varchar(100) not null, age integer not null, weight double precision not null)
- [x] 1.3 Configure `spring.sql.init.mode=always` in `application.properties` for schema initialization

## 2. Domain Model and Repository

- [x] 2.1 Create `Dog` entity class with `@Id` annotation and fields: id (Long), name (String), breed (String), age (Integer), weight (Double)
- [x] 2.2 Create `DogRepository` interface extending `CrudRepository<Dog, Long>`

## 3. DTOs

- [x] 3.1 Create `DogRequest` record with validation annotations: name (@NotBlank, @Size max 100), breed (@NotBlank, @Size max 100), age (@NotNull, @Min 0), weight (@NotNull, @Positive)
- [x] 3.2 Create `DogResponse` record with fields: id, name, breed, age, weight

## 4. REST Controller

- [x] 4.1 Create `DogController` with `@RestController` and `@RequestMapping("/api/dogs")`
- [x] 4.2 Implement POST `/api/dogs` — validate request with `@Valid`, map to entity, save, return 201 with response
- [x] 4.3 Implement GET `/api/dogs` — return all dogs as a list of `DogResponse`
- [x] 4.4 Implement GET `/api/dogs/{id}` — find by ID, return 200 or 404
- [x] 4.5 Implement PUT `/api/dogs/{id}` — validate request, find existing dog or 404, update and return 200
- [x] 4.6 Implement DELETE `/api/dogs/{id}` — find existing dog or 404, delete and return 204

## 5. Error Handling

- [x] 5.1 Add exception handling for validation errors (MethodArgumentNotValidException) returning 400 with field-level error messages
- [x] 5.2 Add handling for dog-not-found cases returning 404

## 6. Integration Tests

- [x] 6.1 Create `DogControllerTest` using `@SpringBootTest` with `WebEnvironment.RANDOM_PORT` and Testcontainers for PostgreSQL
- [x] 6.2 Test POST — successful creation returns 201 with generated ID
- [x] 6.3 Test POST — validation errors return 400 (missing fields, invalid values, name too long)
- [x] 6.4 Test GET list — returns all dogs (200) and empty array when none exist
- [x] 6.5 Test GET by ID — returns dog (200) and 404 for non-existent ID
- [x] 6.6 Test PUT — successful update (200), 404 for non-existent, 400 for invalid data
- [x] 6.7 Test DELETE — successful deletion (204) and 404 for non-existent ID
