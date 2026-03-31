# ToDo-AI

AI-powered Todo Application built with Spring Boot 3.2.3 and Java 17.

## Project Structure

```
src/main/
├── java/com/todoai/
│   ├── controller/        # REST API endpoints
│   ├── service/          # Business logic layer
│   ├── repository/       # Data access layer (Spring Data JPA)
│   ├── model/            # JPA entity definitions
│   ├── dto/              # Data Transfer Objects
│   └── ToDoAiApplication.java  # Main Spring Boot application
└── resources/
    ├── application.properties  # Configuration
```

## Technologies

- **Framework**: Spring Boot 3.2.3
- **Java Version**: 17
- **Database**: H2 (in-memory for development)
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Utilities**: Lombok
- **Validation**: Jakarta Validation

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation

```bash
# Clone the repository
cd /Users/maksim_evmenenko/Projects/ToDo-AI

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080/api`

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### 1. Get All Todos
```
GET /v1/todo/list
```
Returns a list of all todos.

**Response Example:**
```json
[
  {
    "id": 1,
    "title": "Buy groceries",
    "description": "Milk, eggs, bread",
    "completed": false,
    "createdAt": "2026-03-31T10:30:00",
    "updatedAt": "2026-03-31T10:30:00"
  }
]
```

#### 2. Get Active (Incomplete) Todos
```
GET /v1/todo/list/active
```
Returns todos that are not completed.

#### 3. Get Completed Todos
```
GET /v1/todo/list/completed
```
Returns todos that are marked as completed.

#### 4. Get Todo by ID
```
GET /v1/todo/{id}
```
Returns a specific todo by its ID.

**Example:**
```
GET /v1/todo/1
```

#### 5. Create a New Todo
```
POST /v1/todo
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "completed": false,
  "createdAt": "2026-03-31T10:30:00",
  "updatedAt": "2026-03-31T10:30:00"
}
```

#### 6. Update a Todo
```
PUT /v1/todo/{id}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Buy groceries and cook",
  "description": "Updated description",
  "completed": true
}
```

#### 7. Delete a Todo
```
DELETE /v1/todo/{id}
```

**Response (204 No Content)**

## Example Requests Using cURL

```bash
# Get all todos
curl http://localhost:8080/api/v1/todo/list

# Create a new todo
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Complete the tutorial"}'

# Update a todo
curl -X PUT http://localhost:8080/api/v1/todo/1 \
  -H "Content-Type: application/json" \
  -d '{"completed":true}'

# Delete a todo
curl -X DELETE http://localhost:8080/api/v1/todo/1

# Get active todos
curl http://localhost:8080/api/v1/todo/list/active

# Get completed todos
curl http://localhost:8080/api/v1/todo/list/completed
```

## Development

### Database Console
The H2 database console is available at:
```
http://localhost:8080/api/h2-console
```

### Build Commands
```bash
# Clean and build
mvn clean install

# Run tests
mvn test

# Run the application
mvn spring-boot:run

# Package as JAR
mvn package
```

## Code Style and Conventions

- **Naming**: camelCase for variables/methods, PascalCase for classes
- **Annotations**: Use Lombok to reduce boilerplate (@Data, @RequiredArgsConstructor, @Builder)
- **Layer Separation**: Controllers → Services → Repositories
- **Validation**: Use Jakarta Validation annotations on DTOs
- **Timestamps**: Automatic with JPA @PrePersist and @PreUpdate

## Configuration Files

- `.claude` - Claude AI context and project metadata
- `.cursorules` - IDE assistant rules and best practices
- `.claudeignore` - Files to ignore for AI analysis
- `pom.xml` - Maven dependencies and build configuration
- `application.properties` - Spring Boot application configuration

## Future Enhancements

- [ ] User authentication and authorization
- [ ] Categories/Tags for todos
- [ ] Due dates and reminders
- [ ] AI-powered todo suggestions
- [ ] PostgreSQL database integration
- [ ] Docker containerization
- [ ] API documentation with Swagger/OpenAPI

## License

This project is open source and available under the MIT License.
