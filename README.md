# LabFlow - Laboratory Test Management System

Backend application for laboratory test management, built with Spring Boot 4.0.1.

## Project Overview

LabFlow is a system for managing laboratory tests that provides:

- Accepting test requests from walk-in patients and hospitals
- Asynchronous test processing through message queue
- Automatic technician and machine assignment
- Reagent tracking with automatic replacement
- Real-time notifications via WebSocket when tests complete
- Administrative management of test types

## Tech Stack

- Java 17
- Spring Boot 4.0.1
- PostgreSQL 15
- RabbitMQ 3.x
- Lombok
- SpringDoc OpenAPI 2.1.0
- Testcontainers 1.19+

## Prerequisites for Local Development

### Required Software

- **Java 17** or later
- **Docker** and **Docker Compose** (for running external services)
- **Maven** 3.6+ (or use the included Maven Wrapper)

### Starting External Services

Before running the application, start the PostgreSQL and RabbitMQ containers:

```bash
docker-compose up -d
```

This command will start:

- **PostgreSQL** on port `5432` - Database
- **RabbitMQ** on port `5672` - Message broker
- **RabbitMQ Management UI** on port `15672` - Web interface for RabbitMQ

#### Local Development Credentials

**PostgreSQL:**

- Host: `localhost:5432`
- Database: `labflow`
- Username: `labflow`
- Password: `labflow`

**RabbitMQ:**

- Host: `localhost:5672`
- Username: `guest`
- Password: `guest`
- Management UI: http://localhost:15672

### Running the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Using installed Maven:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` with the `local` profile active.

## Project Structure

```
src/main/java/com/eclept/andjelazoric_eclept_be_labflow/
|
|-- annotation/              # Annotation 
|-- config/                  # Configuration (RabbitMQ, WebSocket, Web)
|-- controller/              # REST controllers
|   |-- TestRequestController.java    # API for test requests
|   |-- LabAdminController.java       # Admin API for test types
|
|-- dto/                     # Data Transfer Objects
|   |-- common/              # Shared DTOs
|   |-- request/             # Request DTOs
|   |-- response/            # Response DTOs
|
|-- entity/                  # JPA entities
|   |-- TestRequest.java     # Test request
|   |-- TestType.java        # Test type definition
|   |-- Technician.java      # Technician/machine
|
|-- enums/                   # Enumerations
|   |-- TestStatus.java      # Test statuses (RECEIVED, PROCESSING, COMPLETED, REJECTED)
|
|-- exception/               # Custom exceptions and global handler
|-- interceptors/            # HTTP interceptors (admin authorization)
|-- loader/                  # Initial data loader for local profile
|-- mapper/                  # Entity-DTO mappers
|-- notification/            # WebSocket notifications
|-- processor/               # RabbitMQ consumer for test processing
|-- queue/                   # RabbitMQ producer
|-- repository/              # JPA repositories
|-- service/                 # Business layer (interfaces and implementations)
```

## System Architecture

### Test Request Processing Flow

1. Client sends a POST request to `/api/tests` with test data
2. Request is saved in the database with status `RECEIVED`
3. Request ID is sent to RabbitMQ queue (`labflow-tests`)
4. `TestProcessor` (consumer) picks up the request from the queue
5. An available technician with sufficient reagents is assigned
6. Status changes to `PROCESSING`, test execution begins
7. Upon completion, status changes to `COMPLETED`
8. If the request is from a hospital (not walk-in), a WebSocket notification is sent

### Message Queue Configuration

- **Main queue:** `labflow-tests` - for test processing
- **Error queue:** `labflow-tests-errors` - for failed tests
- **Exchange:** `labflow-exchange` (DirectExchange)

### WebSocket

- **Endpoint:** `/ws` (SockJS enabled)
- **Topic:** `/topic/tests/{testId}` - notifications when a test completes

## API Endpoints

### Test Requests

**POST /api/tests**

- Creates a new test request
- No authorization required

**GET /api/tests/{id}/status**

- Returns the current status of a test
- No authorization required

**GET /api/tests**

- Returns a paginated list of all test requests
- Requires Admin authorization

### Admin - Test Types

**GET /api/admin/test-types**

- Returns all test types
- No authorization required

**POST /api/admin/test-types**

- Creates a new test type
- Requires Admin authorization

**PUT /api/admin/test-types/{id}**

- Updates an existing test type
- Requires Admin authorization

**DELETE /api/admin/test-types/{id}**

- Deletes a test type
- Requires Admin authorization

### Authorization

Admin endpoints require the `X-API-KEY` header with the value defined in `labflow.admin.api-key` property (default:
`admin`).

### Path Parameters / Notes on IDs

**Note:**  
For all endpoints that use `{id}` (e.g., `/api/tests/{id}/status`, `/api/admin/test-types/{id}`)

**You must replace `{id}` with a valid ID from your environment.**

In Postman, you can use **collection or environment variables**, for example:

- `{{testRequestId}}` — for a test request ID
- `{{testTypeId}}` — for a test type ID

**Example usage:**

```http
GET    http://localhost:8080/api/tests/{{testRequestId}}/status
PUT    http://localhost:8080/api/admin/test-types/{{testTypeId}}
DELETE http://localhost:8080/api/admin/test-types/{{testTypeId}}
```

## Profiles

- **local** - Local development, DDL auto: update
- **dev** - Development environment, DDL auto: update
- **prod** - Production, DDL auto: validate
- **test** - Testing with Testcontainers

## Initial Data (local profile)

When running with the `local` profile, the following data is automatically created:

**Test Types:**

- Blood Test (60s processing time, 20 reagent units)
- Urine Test (90s processing time, 30 reagent units)
- PCR Test (180s processing time, 100 reagent units)
- Allergy Panel (240s processing time, 150 reagent units)

**Technicians:**

- 5 technicians with machines, each with 500 reagent units

### Postman Collection

A ready-to-use Postman collection is provided in the `postman/` folder.

**Import into Postman:**

1. Open Postman.
2. Click **Import** → select `postman/labflow-api-collection.json`.
3. Optionally, import environment variables from `postman/labflow-api-environment.json`.
4. Replace placeholder variables like `{{testTypeId}}` or `{{testRequestId}}` with valid values in your environment.

## Running Tests

Run all tests:

```bash
./mvnw test
```

Run only unit tests:

```bash
./mvnw test -Dtest="**/unit/**"
```

Run only integration tests:

```bash
./mvnw test -Dtest="**/integration/**"
```

Integration tests use **Testcontainers** for PostgreSQL and RabbitMQ, so no external services need to be running.

## Configuration

Main configuration options in `application.properties`:

- `server.port` - Application port (default: 8080)
- `labflow.reagentReplacementTimeMinutes` - Reagent replacement time in minutes (default: 1 for local, 4 for others)
- `labflow.admin.api-key` - API key for admin endpoints (default: admin)
- `spring.profiles.active` - Active profile (default: local)

## Stopping Services

Stop the containers:

```bash
docker-compose down
```

Stop and remove data volumes:

```bash
docker-compose down -v
```