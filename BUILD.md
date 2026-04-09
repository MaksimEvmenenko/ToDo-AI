# BUILD & SETUP GUIDE

## Quick Start

```bash
# 1. Build the project
./gradlew clean build -x test

# 2. Setup database
mysql -u root -p
CREATE DATABASE todoai;
USE todoai;
source schema.sql;

# 3. Run the application
./gradlew bootRun
```

Application will be available at: `http://localhost:8080/api`

---

## Prerequisites

- **Java**: 17 or higher
- **Gradle**: 7.6+ (included via gradlew)
- **Database**: MySQL 8.0+ or PostgreSQL 12+
- **Memory**: 2GB minimum

Verify Java version:
```bash
java -version
```

---

## Build with Gradle

```bash
# Clean build
./gradlew clean build

# Build without tests
./gradlew clean build -x test

# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Package as JAR
./gradlew bootJar
```

---

## Database Setup

### MySQL Setup

```bash
# Create database
mysql -u root -p
CREATE DATABASE todoai;
USE todoai;
source schema.sql;
```

### PostgreSQL Setup

```bash
createdb todoai
psql todoai < schema.sql
```

### Update Configuration

Edit `src/main/resources/application.properties`:

**For MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todoai
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**For PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/todoai
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

---

## Project Structure

```
src/main/
├── java/com/todoai/
│   ├── controller/          # REST API endpoints
│   ├── service/            # Business logic layer
│   ├── repository/         # Data access layer
│   ├── model/              # JPA entities
│   ├── dto/                # Data Transfer Objects
│   ├── config/             # Configuration classes
│   ├── security/           # Security components
│   └── ToDoAiApplication.java
└── resources/
    └── application.properties
```

---

## Gradle Configuration

### Key Dependencies

```gradle
// Web & REST API
implementation 'org.springframework.boot:spring-boot-starter-web'

// Database
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

// Security
implementation 'org.springframework.boot:spring-boot-starter-security'

// JWT
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'

// Utilities
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

### Build Profiles

```bash
# Development (default)
./gradlew bootRun

# Production build
./gradlew clean build -Pprod

# Test-only build
./gradlew test
```

---

## Troubleshooting Build Issues

### Issue: Build fails with "Java version mismatch"
**Solution:** Verify Java 17+
```bash
java -version
# Should show version 17 or higher
```

### Issue: "gradle command not found"
**Solution:** Use the gradle wrapper
```bash
./gradlew build  # Use this instead of gradle build
```

### Issue: Compilation errors
**Solution:** Clean and rebuild
```bash
./gradlew clean build -x test
```

### Issue: Port 8080 already in use
**Solution:** Change port in application.properties
```properties
server.port=8081
```

---

## Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests TodoServiceTest

# Run with coverage
./gradlew test jacocoTestReport

# Skip tests during build
./gradlew build -x test
```

---

## IDE Setup

### IntelliJ IDEA
1. Open project: File → Open → Select project folder
2. Configure JDK: File → Project Structure → Project → Set JDK 17
3. Enable Gradle: File → Settings → Build Tools → Gradle → Use Gradle wrapper
4. Install Lombok plugin: File → Settings → Plugins → Search "Lombok" → Install

### VS Code
1. Install "Extension Pack for Java"
2. Install "Gradle for Java"
3. Open project folder
4. Gradle tasks will appear in sidebar

### Eclipse
1. File → Import → Gradle → Existing Gradle Project
2. Select project root
3. Install Lombok: Help → Install New Software → Add `https://projectlombok.org/p2`

---

## Deployment

### Local JAR Deployment
```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/todo-ai-1.0.0.jar
```

### Docker (Optional)
```bash
# Build Docker image (requires Dockerfile)
docker build -t todo-ai .

# Run container
docker run -p 8080:8080 todo-ai
```

### Production Configuration
Before deploying:
- [ ] Change JWT secret to secure random value
- [ ] Update database credentials
- [ ] Enable HTTPS
- [ ] Configure logging to WARN level
- [ ] Set environment profile to production
- [ ] Configure database backups

```properties
spring.profiles.active=production
logging.level.root=WARN
```

---

## Development Workflow

```bash
# 1. Create feature branch
git checkout -b feature/new-feature

# 2. Make changes and test locally
./gradlew bootRun

# 3. Run tests
./gradlew test

# 4. Build
./gradlew clean build

# 5. Commit and push
git commit -am "Add new feature"
git push origin feature/new-feature
```

---

## Useful Gradle Tasks

```bash
# List all available tasks
./gradlew tasks

# Show task dependencies
./gradlew dependencies

# Clean build artifacts
./gradlew clean

# Display info about current build
./gradlew -v
```

---

## Need Help?

- See API.md for API documentation
- See SECURITY.md for security configuration
- See DEVELOPMENT.md for development guidelines
- See README.md for project overview

