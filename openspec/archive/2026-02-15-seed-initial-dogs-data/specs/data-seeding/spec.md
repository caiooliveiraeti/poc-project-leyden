## ADDED Requirements

### Requirement: Seed dogs on first startup
The system SHALL automatically insert approximately 1000 dog records into the database when the application starts and the `dog` table is empty.

#### Scenario: First startup with empty database
- **WHEN** the application starts and the `dog` table contains zero records
- **THEN** the system inserts ~1000 dog records with varied breeds, names, ages, and weights

#### Scenario: Subsequent startup with existing data
- **WHEN** the application starts and the `dog` table already contains records
- **THEN** the system SHALL NOT insert any additional records

### Requirement: Seeded dogs have diverse breeds
The system SHALL seed dogs across at least 20 different breeds with realistic name, age, and weight distributions.

#### Scenario: Breed variety in seeded data
- **WHEN** the seeding process completes
- **THEN** the inserted dogs SHALL span at least 20 distinct breeds

#### Scenario: Realistic attribute ranges
- **WHEN** the seeding process completes
- **THEN** each dog SHALL have an age between 1 and 15 years and a weight appropriate to its breed