## ADDED Requirements

### Requirement: Create a dog
The system SHALL accept a POST request to `/api/dogs` with a JSON body containing name, breed, age, and weight, and SHALL persist a new dog record and return it with a generated ID.

#### Scenario: Successful creation
- **WHEN** a POST request is sent to `/api/dogs` with body `{"name": "Rex", "breed": "Labrador", "age": 3, "weight": 25.5}`
- **THEN** the system returns HTTP 201 with the created dog including a generated `id`

#### Scenario: Missing required field
- **WHEN** a POST request is sent to `/api/dogs` with body `{"name": "Rex", "breed": "Labrador"}`
- **THEN** the system returns HTTP 400 with a validation error indicating the missing fields

#### Scenario: Invalid field values
- **WHEN** a POST request is sent to `/api/dogs` with body `{"name": "", "breed": "Labrador", "age": -1, "weight": 0}`
- **THEN** the system returns HTTP 400 with validation errors for each invalid field

### Requirement: List all dogs
The system SHALL accept a GET request to `/api/dogs` and SHALL return all dog records as a JSON array.

#### Scenario: List with existing dogs
- **WHEN** dogs exist in the database and a GET request is sent to `/api/dogs`
- **THEN** the system returns HTTP 200 with a JSON array containing all dogs

#### Scenario: List with no dogs
- **WHEN** no dogs exist in the database and a GET request is sent to `/api/dogs`
- **THEN** the system returns HTTP 200 with an empty JSON array

### Requirement: Get a dog by ID
The system SHALL accept a GET request to `/api/dogs/{id}` and SHALL return the dog record matching the given ID.

#### Scenario: Dog exists
- **WHEN** a dog with ID 1 exists and a GET request is sent to `/api/dogs/1`
- **THEN** the system returns HTTP 200 with the dog's data

#### Scenario: Dog does not exist
- **WHEN** no dog with ID 999 exists and a GET request is sent to `/api/dogs/999`
- **THEN** the system returns HTTP 404

### Requirement: Update a dog
The system SHALL accept a PUT request to `/api/dogs/{id}` with a JSON body and SHALL replace the dog record with the provided data.

#### Scenario: Successful update
- **WHEN** a dog with ID 1 exists and a PUT request is sent to `/api/dogs/1` with body `{"name": "Rex Updated", "breed": "Labrador", "age": 4, "weight": 26.0}`
- **THEN** the system returns HTTP 200 with the updated dog data

#### Scenario: Update non-existent dog
- **WHEN** no dog with ID 999 exists and a PUT request is sent to `/api/dogs/999`
- **THEN** the system returns HTTP 404

#### Scenario: Update with invalid data
- **WHEN** a dog with ID 1 exists and a PUT request is sent to `/api/dogs/1` with body `{"name": "", "breed": "", "age": -1, "weight": 0}`
- **THEN** the system returns HTTP 400 with validation errors

### Requirement: Delete a dog
The system SHALL accept a DELETE request to `/api/dogs/{id}` and SHALL permanently remove the dog record.

#### Scenario: Successful deletion
- **WHEN** a dog with ID 1 exists and a DELETE request is sent to `/api/dogs/1`
- **THEN** the system returns HTTP 204 and the dog is permanently removed from the database

#### Scenario: Delete non-existent dog
- **WHEN** no dog with ID 999 exists and a DELETE request is sent to `/api/dogs/999`
- **THEN** the system returns HTTP 404

### Requirement: Dog data model
The system SHALL store dogs with the following fields: id (auto-generated long), name (string, required, max 100 chars), breed (string, required, max 100 chars), age (integer, required, >= 0), and weight (double, required, > 0).

#### Scenario: Dog fields are persisted correctly
- **WHEN** a dog is created with name "Buddy", breed "Golden Retriever", age 5, weight 30.0
- **THEN** the dog is stored in the database with all fields matching the input and an auto-generated ID

### Requirement: Input validation
The system SHALL validate all input fields on create and update operations and SHALL return HTTP 400 with descriptive error messages for invalid input.

#### Scenario: Name exceeds max length
- **WHEN** a POST request is sent with a name longer than 100 characters
- **THEN** the system returns HTTP 400 with a validation error for the name field

#### Scenario: Negative age
- **WHEN** a POST request is sent with age set to -1
- **THEN** the system returns HTTP 400 with a validation error for the age field

#### Scenario: Zero or negative weight
- **WHEN** a POST request is sent with weight set to 0
- **THEN** the system returns HTTP 400 with a validation error for the weight field
