# Development Guide for ToDo-AI

## Project Setup

### Prerequisites
- Java 17 JDK
- Maven 3.6+
- Git
- IDE: IntelliJ IDEA, Eclipse, or VS Code with Spring Boot extensions

### Initial Setup

```bash
# Navigate to project directory
cd /Users/maksim_evmenenko/Projects/ToDo-AI

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

## Project Architecture

### Layer Overview

#### 1. Controller Layer (`controller`)
- Handles HTTP requests and responses
- Maps URL paths to service methods
- Manages HTTP status codes and headers
- Validates request parameters
- No business logic

#### 2. Service Layer (`service`)
- Contains all business logic
- Manages transactions
- Orchestrates data operations
- Handles exceptions
- Returns DTOs to controllers

#### 3. Repository Layer (`repository`)
- Data access through Spring Data JPA
- Database queries
- No business logic, only data operations

#### 4. Model Layer (`model`)
- JPA entity classes
- Database mapping annotations
- Lifecycle callbacks (@PrePersist, @PreUpdate)

#### 5. DTO Layer (`dto`)
- Data Transfer Objects for API communication
- Request/response validation
- Decouples entities from API

## Development Workflow

### Adding a New Feature

1. **Create Entity (if needed)**
   ```bash
   src/main/java/com/todoai/model/YourEntity.java
   ```

2. **Create DTO**
   ```bash
   src/main/java/com/todoai/dto/YourEntityDTO.java
   ```

3. **Create Repository**
   ```bash
   src/main/java/com/todoai/repository/YourEntityRepository.java
   ```

4. **Create Service**
   ```bash
   src/main/java/com/todoai/service/YourEntityService.java
   ```

5. **Create Controller**
   ```bash
   src/main/java/com/todoai/controller/YourEntityController.java
   ```

### Example: Adding a Category Feature

```java
// 1. Model
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "category")
    private List<Todo> todos;
}

// 2. DTO
@Data
public class CategoryDTO {
    private Long id;
    @NotBlank
    private String name;
}

// 3. Repository
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}

// 4. Service
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    
    public CategoryDTO createCategory(CategoryDTO dto) {
        // Implementation
    }
}

// 5. Controller
@RestController
@RequestMapping("/v1/category")
public class CategoryController {
    private final CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto));
    }
}
```

## API Design Guidelines

### URL Patterns
```
GET    /v1/resource           - List all
GET    /v1/resource/{id}      - Get one
POST   /v1/resource           - Create
PUT    /v1/resource/{id}      - Update
DELETE /v1/resource/{id}      - Delete
```

### Response Format
- All responses are JSON
- Use appropriate HTTP status codes
- Include timestamps for tracking

### Error Handling
Consider creating a global exception handler:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of("error", e.getMessage())
        );
    }
}
```

## Testing

### Unit Tests
```bash
mvn test
```

### Example Unit Test
```java
@SpringBootTest
public class TodoServiceTest {
    
    @MockBean
    private TodoRepository todoRepository;
    
    @Autowired
    private TodoService todoService;
    
    @Test
    public void testCreateTodo() {
        TodoDTO dto = TodoDTO.builder().title("Test").build();
        // Test logic
    }
}
```

## Database Management

### H2 Console
Access at: `http://localhost:8080/api/h2-console`

### Schema Generation
- DDL strategy: `create-drop` (for development)
- Change in `application.properties`: `spring.jpa.hibernate.ddl-auto`

### Add Initial Data
Create `data.sql` in `src/main/resources/`:

```sql
INSERT INTO todos (title, description, completed, created_at, updated_at) 
VALUES ('Sample Todo', 'Description', false, NOW(), NOW());
```

## Build and Deployment

### Local Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Package as JAR
```bash
mvn package
java -jar target/todo-ai-1.0.0.jar
```

## Code Quality Standards

### Checkstyle
- Line length: Max 120 characters
- Indentation: 4 spaces
- No trailing whitespace

### Best Practices
- Use constructor injection (@RequiredArgsConstructor)
- Add JavaDoc to public methods
- Use meaningful variable names
- Keep methods focused (single responsibility)
- Use Optional instead of null checks

## Debugging

### Enable Debug Logging
In `application.properties`:
```properties
logging.level.com.todoai=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Using Spring Boot DevTools
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

## Useful Maven Commands

```bash
# Clean build
mvn clean

# Compile
mvn compile

# Run tests
mvn test

# Run specific test
mvn test -Dtest=TodoServiceTest

# Build package
mvn package

# Skip tests during build
mvn package -DskipTests

# Display dependency tree
mvn dependency:tree

# Check for outdated dependencies
mvn versions:display-dependency-updates
```

## IDE Configuration

### IntelliJ IDEA
1. Import as Maven project
2. Enable annotation processing (Lombok)
3. Set Java version to 17
4. Configure code style for Spring

### VS Code
- Install Java Extension Pack
- Install Spring Boot Extension Pack
- Install Lombok Annotations Support

## Common Issues and Solutions

### Issue: Build fails with "cannot find symbol"
**Solution**: Run `mvn clean install` and refresh IDE

### Issue: Port 8080 already in use
**Solution**: Change port in `application.properties`
```properties
server.port=8081
```

### Issue: Lombok annotations not recognized
**Solution**: Enable annotation processing in IDE settings

## Performance Considerations

- Use pagination for large datasets
- Implement caching for frequently accessed data
- Use proper database indices
- Optimize N+1 queries with @EntityGraph

## Security Considerations

- Validate all inputs
- Use HTTPS in production
- Implement authentication/authorization
- Sanitize user input
- Use parameterized queries (JPA does this automatically)

