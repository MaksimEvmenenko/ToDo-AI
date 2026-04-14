# ToDo-AI

AI-powered Todo Application with Spring Boot 3.2.3, JWT Authentication, and Reminder Scheduling.

**Status**: ✅ Ready to build and deploy

---

## Quick Start

```bash
# 1. Build
./gradlew clean build -x test

# 2. Setup database
mysql -u root -p
CREATE DATABASE todoai;
USE todoai;
source schema.sql;

# 3. Run
./gradlew bootRun
```

App available at: `http://localhost:8080/api`

---

## Key Features

- ✅ **User Authentication**: JWT-based login/signup with refresh tokens
- ✅ **Todo Management**: Create, read, update, delete todos
- ✅ **Reminders**: Schedule reminders with automatic notifications
- ✅ **Security**: BCrypt password hashing, Spring Security
- ✅ **REST API**: Full-featured API with proper error handling
- ✅ **Database**: MySQL/PostgreSQL with automatic schema migration

---

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2.3 |
| Java | 17+ |
| Database | MySQL 8.0+ / PostgreSQL 12+ |
| Build | Gradle 7.6+ |
| ORM | JPA/Hibernate |
| Auth | JWT (HS512) |
| Password | BCrypt |

---

## Project Structure

```
src/main/
├── java/com/todoai/
│   ├── controller/     # REST API endpoints
│   ├── service/        # Business logic
│   ├── repository/     # Data access layer
│   ├── model/          # JPA entities
│   ├── dto/            # Data Transfer Objects
│   ├── config/         # Configuration classes
│   ├── security/       # Security components
│   └── ToDoAiApplication.java
└── resources/
    └── application.properties
```

---

## Documentation

- **[BUILD.md](BUILD.md)** - Building, setup, deployment
- **[API.md](API.md)** - Complete API documentation with examples
- **[SECURITY.md](SECURITY.md)** - Authentication, configuration, best practices
- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Development guidelines and architecture
- **[schema.sql](schema.sql)** - Database schema

---

## API Endpoints

### Authentication (Public)
- `POST /v1/auth/signup` - Register new user
- `POST /v1/auth/login` - Login and get tokens
- `POST /v1/auth/refresh` - Refresh access token
- `GET /v1/auth/me` - Get current user (protected)

### Todos (All Protected)
- `GET /v1/todo/list` - Get all todos
- `GET /v1/todo/list/active` - Get incomplete todos
- `GET /v1/todo/list/completed` - Get completed todos
- `GET /v1/todo/{id}` - Get specific todo
- `POST /v1/todo` - Create todo
- `PUT /v1/todo/{id}` - Update todo
- `DELETE /v1/todo/{id}` - Delete todo

### Reminders (All Protected)
- `GET /v1/reminder/list` - Get all reminders
- `GET /v1/reminder/{id}` - Get specific reminder
- `POST /v1/reminder` - Create reminder
- `PUT /v1/reminder/{id}` - Update reminder
- `DELETE /v1/reminder/{id}` - Delete reminder
- `POST /v1/reminder/{id}/send` - Trigger notification

---

## Quick Examples

### Sign Up
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username":"user",
    "email":"user@example.com",
    "password":"SecurePass123!"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"SecurePass123!"}'
```

### Create Todo (with token)
```bash
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy groceries","description":"Milk, eggs"}'
```

See [API.md](API.md) for complete API documentation.

---

## Setup & Deployment

### Prerequisites
- Java 17+
- Gradle (or use `./gradlew`)
- MySQL or PostgreSQL

### Build
```bash
./gradlew clean build -x test
```

### Database
```bash
mysql -u root -p todoai < schema.sql
```

### Run
```bash
./gradlew bootRun
```

See [BUILD.md](BUILD.md) for detailed setup instructions.

---

## Security

- **Authentication**: JWT with HS512 signature
- **Password**: BCrypt hashing with unique salts
- **Authorization**: Spring Security with role-based access
- **Input Validation**: Jakarta Validation on all endpoints
- **CORS**: Configured for localhost (update for production)

**Production Checklist**: See [SECURITY.md](SECURITY.md)

Key steps:
- [ ] Change `jwt.secret` to secure random value
- [ ] Enable HTTPS
- [ ] Update CORS origins
- [ ] Configure database encryption

---

## Configuration

### Application Properties

Edit `src/main/resources/application.properties`:

**Database (MySQL)**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todoai
spring.datasource.username=root
spring.datasource.password=password
```

**JWT**:
```properties
jwt.secret=your-secret-key-change-this-in-production-minimum-32-chars-for-HS512
jwt.expiration=3600000
jwt.refresh-expiration=86400000
```

---

## Development

### Run Tests
```bash
./gradlew test
```

### Run Application
```bash
./gradlew bootRun
```

### Build JAR
```bash
./gradlew bootJar
java -jar build/libs/todo-ai-1.0.0.jar
```

See [DEVELOPMENT.md](DEVELOPMENT.md) for development guidelines.

---

## GitHub Actions - CI/CD

The project includes automated GitHub Actions workflows:

### Build and Test Workflow
**File**: `.github/workflows/build-and-test.yml`

**Triggers**: 
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Steps**:
1. ✅ Checkout code
2. ✅ Setup JDK 17
3. ✅ Build with Gradle
4. ✅ Run unit tests
5. ✅ Upload test results
6. ✅ Publish test reports
7. ✅ Run code quality checks

**Artifacts**:
- `test-results-17` - JUnit XML test results
- `build-reports-17` - Build reports and test summaries

**Status Badge**:
```markdown
[![Build and Test](https://github.com/YOUR_USERNAME/ToDo-AI/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/YOUR_USERNAME/ToDo-AI/actions/workflows/build-and-test.yml)
```

---

## Troubleshooting

**Build fails**: `./gradlew clean build -x test`
**Port in use**: Change `server.port` in application.properties
**DB connection fails**: Check database credentials
**Auth errors**: Verify JWT secret length (min 32 chars)

See [BUILD.md](BUILD.md) troubleshooting section for more.

---

## License

MIT License - See LICENSE file for details

---

**Need Help?**
- Build & Setup: See [BUILD.md](BUILD.md)
- API Reference: See [API.md](API.md)
- Security Config: See [SECURITY.md](SECURITY.md)
- Development: See [DEVELOPMENT.md](DEVELOPMENT.md)
