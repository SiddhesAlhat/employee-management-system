# Employee Management System

A Spring Boot REST API for managing employee records, secured with JWT authentication.
Built for the Zest India IT Pvt. Ltd. Java assessment.

## Tech Stack
- Java 8+
- Spring Boot 2.7
- Spring Data JPA / Hibernate
- MySQL
- Spring Security + JWT (jjwt)
- Maven
- JUnit 5 + Mockito (unit tests), H2 (test database)

## Features
- User registration and login with BCrypt password hashing
- JWT-secured endpoints (all `/api/employees/**` routes require a valid token)
- Employee CRUD: create, read (single + paginated/sorted list), update, delete
- Centralized validation and error handling
- Unit tests for service layer, repository layer, and controller layer

## Project Structure
```
src/main/java/com/zest/employeemanagement/
├── config/         # Security config, JWT filter, entry point
├── controller/      # REST controllers (Auth, Employee)
├── dto/             # Request/response DTOs
├── entity/          # JPA entities (User, Employee)
├── exception/        # Custom exceptions + global handler
├── repository/      # Spring Data JPA repositories
├── security/         # JwtUtil, UserPrincipal
└── service/          # Business logic
```

## Setup

### 1. Create the database
MySQL will auto-create the schema on first run (`createDatabaseIfNotExist=true`), but the DB user needs privileges. Just make sure MySQL is running locally.

### 2. Configure credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
jwt.secret=<a-random-secret-at-least-32-characters-long>
```

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```
The API starts on `http://localhost:8080`.

### 4. Run tests
```bash
mvn test
```
Tests run against an in-memory H2 database, so no MySQL connection is required for `mvn test`.

## API Endpoints

| Method | Endpoint                  | Auth required | Description                    |
|--------|----------------------------|----------------|--------------------------------|
| POST   | `/api/auth/register`       | No             | Register a new user            |
| POST   | `/api/auth/login`          | No             | Login, returns JWT token       |
| POST   | `/api/employees`           | Yes            | Create an employee              |
| GET    | `/api/employees`           | Yes            | List employees (paginated/sorted) |
| GET    | `/api/employees/{id}`      | Yes            | Get employee by id              |
| PUT    | `/api/employees/{id}`      | Yes            | Update employee                 |
| DELETE | `/api/employees/{id}`      | Yes            | Delete employee                 |

### Pagination & sorting query params (on GET `/api/employees`)
- `page` (default 0)
- `size` (default 10)
- `sortBy` (default `id`) — e.g. `name`, `salary`, `dateOfJoining`
- `direction` (default `asc`) — `asc` or `desc`

Example:
```
GET /api/employees?page=0&size=5&sortBy=salary&direction=desc
```

### Authentication flow
1. `POST /api/auth/register` with `{ "username", "email", "password" }`
2. `POST /api/auth/login` with `{ "username", "password" }` → returns `{ "token": "..." }`
3. Pass the token on every protected request:
   ```
   Authorization: Bearer <token>
   ```

## Testing with Postman
A ready-to-import collection is included: `postman_collection.json`.
It auto-captures the JWT token from the login response into a collection variable (`jwtToken`) and reuses it in subsequent requests. Import it into Postman, run **Register → Login** first, then the Employee CRUD requests.

## Pushing to GitHub
```bash
git init
git add .
git commit -m "Employee Management System with JWT auth"
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```

## Notes
- Passwords are hashed with BCrypt before storage — never stored in plain text.
- Sessions are stateless; JWT is validated on every request via a custom filter.
- `spring.jpa.hibernate.ddl-auto=update` auto-creates/updates tables on startup — fine for an assessment, not recommended as-is for production (use migrations like Flyway/Liquibase instead).
