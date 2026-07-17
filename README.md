# Digital Archive - Backend

Backend REST API for a Digital Document Management System.

The application supports document indexing, PDF storage and preview, advanced document search, pagination, JWT authentication and role-based access control.

## Main Features

- User authentication with JWT
- Role-based authorization
- Creation and indexing of documents
- PDF upload and local file storage
- PDF retrieval and preview
- Document search using multiple criteria
- Pagination of search results
- Document editing and deletion
- User creation by an administrator
- DTO-based communication
- Request validation and centralized exception handling
- BCrypt password hashing

## User Roles

| Role | Permissions |
|---|---|
| `ADMIN` | Full access, user creation and document deletion |
| `INDEXER` | Create, index, search, open and edit documents |
| `OPERATOR` | Search and open existing documents |

## Technologies

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- BCrypt
- MySQL
- Maven
- Jakarta Bean Validation

## Project Architecture

The backend follows a layered architecture:

- `controller` — REST endpoints
- `service` — business logic
- `repository` — database access
- `entity` — JPA entities
- `dto` — request and response objects
- `security` — JWT authentication and authorization
- `exception` — centralized error handling

## Requirements

Before running the application, install:

- Java 17 or newer
- MySQL Server
- Git

The Maven Wrapper is included in the project.

## Database Setup

Create the MySQL database:

```sql
CREATE DATABASE dms;
```

The application uses Hibernate to create and update the required tables.

## Application Configuration

The real `application.properties` file is not stored on GitHub because it contains local database credentials and the JWT secret.

Copy:

```text
src/main/resources/application.properties.example
```

and create:

```text
src/main/resources/application.properties
```

Replace the placeholder values with your local MySQL credentials.

The JWT secret must contain at least 32 characters:

```properties
jwt.secret=YOUR_PRIVATE_SECRET_WITH_AT_LEAST_32_CHARACTERS
```

Never commit the real database password or JWT secret.

## Running the Backend

On Windows:

```bash
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

## Initial Administrator

An initial user with the `ADMIN` role must exist in the database before additional users can be created through the application.

Passwords are stored as BCrypt hashes and must never be stored as plain text.

## PDF Storage

Uploaded PDF documents are stored locally in:

```text
uploads/
```

Real PDF files are excluded from GitHub. Only the empty folder structure is included.

## Testing

Run the backend test suite on Windows:

```bash
.\mvnw.cmd test
```

Current result:

```text
Tests run: 1, Failures: 0, Errors: 0
BUILD SUCCESS
```

## Frontend

The Angular frontend is available here:

[Digital Archive Frontend](https://github.com/belojevic89/digital-archive-frontend)

## Project Status

This project was developed as a final Java Developer academic project and demonstrates a complete document-management workflow using Spring Boot, Angular and MySQL.

## Author

Bojana Belojevic