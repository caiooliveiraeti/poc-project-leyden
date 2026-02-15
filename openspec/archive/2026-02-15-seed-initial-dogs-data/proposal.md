## Why

The application starts with an empty database, making it hard to demo, test, or explore the API. Seeding ~1000 dogs of varied breeds on first startup provides a realistic dataset immediately.

## What Changes

- Add a data seeder that runs on application startup and inserts ~1000 dogs with diverse breeds, names, ages, and weights
- The seeder should only run when the `dog` table is empty (first-time startup), to avoid duplicating data on restarts

## Capabilities

### New Capabilities
- `data-seeding`: Automatic population of the database with ~1000 dogs of different breeds on first startup

### Modified Capabilities

## Impact

- New Spring component for seeding logic
- Runs at startup via `ApplicationRunner` or similar Spring lifecycle hook
- No API changes, no schema changes
- Startup time will increase slightly on first run due to bulk insert