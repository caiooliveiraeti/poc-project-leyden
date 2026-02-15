## 1. Data Seeder Implementation

- [x] 1.1 Create `DogSeeder` class implementing `ApplicationRunner` with breed data arrays (at least 20 breeds with name pools and weight/age ranges)
- [x] 1.2 Implement idempotency check — query dog count and skip seeding if table is not empty
- [x] 1.3 Generate ~1000 dog records with randomized names, breeds, ages, and weights using the predefined data
- [x] 1.4 Batch insert generated dogs using `JdbcClient`

## 2. Testing

- [x] 2.1 Write integration test verifying ~1000 dogs are seeded on startup with empty database
- [x] 2.2 Write integration test verifying seeder does not insert when dogs already exist
- [x] 2.3 Verify seeded data has at least 20 distinct breeds