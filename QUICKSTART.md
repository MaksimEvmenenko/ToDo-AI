# Quick Start Guide - ToDo-AI

## 🚀 Start the Application

```bash
# 1. Navigate to the project directory
cd /Users/maksim_evmenenko/Projects/ToDo-AI

# 2. Build the project (first time)
mvn clean install

# 3. Run the application
mvn spring-boot:run
```

The API will be available at: **http://localhost:8080/api**

## 🧪 Test the API

### Using cURL

```bash
# 1. Get all todos (should be empty initially)
curl http://localhost:8080/api/v1/todo/list

# 2. Create a new todo
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy groceries","description":"Milk, eggs, bread"}'

# 3. Get all todos again (should see your new todo)
curl http://localhost:8080/api/v1/todo/list

# 4. Create another todo
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Complete the tutorial"}'

# 5. Get a specific todo (replace 1 with actual ID)
curl http://localhost:8080/api/v1/todo/1

# 6. Update a todo (mark as completed)
curl -X PUT http://localhost:8080/api/v1/todo/1 \
  -H "Content-Type: application/json" \
  -d '{"completed":true}'

# 7. Get active (incomplete) todos
curl http://localhost:8080/api/v1/todo/list/active

# 8. Get completed todos
curl http://localhost:8080/api/v1/todo/list/completed

# 9. Delete a todo
curl -X DELETE http://localhost:8080/api/v1/todo/1
```

### Using Postman or similar tools

1. **Create Collection**: "ToDo-AI"
2. **Import Requests**:
   - GET: `http://localhost:8080/api/v1/todo/list`
   - POST: `http://localhost:8080/api/v1/todo`
   - GET: `http://localhost:8080/api/v1/todo/{id}`
   - PUT: `http://localhost:8080/api/v1/todo/{id}`
   - DELETE: `http://localhost:8080/api/v1/todo/{id}`
   - GET: `http://localhost:8080/api/v1/todo/list/active`
   - GET: `http://localhost:8080/api/v1/todo/list/completed`

## 📊 Access the Database Console

H2 Database Console: **http://localhost:8080/api/h2-console**

- **JDBC URL**: `jdbc:h2:mem:tododb`
- **User Name**: `sa`
- **Password**: (leave blank)

## 📁 Project Structure

```
src/main/
├── java/com/todoai/
│   ├── controller/TodoController.java          # REST endpoints
│   ├── service/TodoService.java               # Business logic
│   ├── repository/TodoRepository.java         # Data access
│   ├── model/Todo.java                        # Entity
│   ├── dto/TodoDTO.java                       # Data transfer object
│   └── ToDoAiApplication.java                 # Entry point
└── resources/
    └── application.properties                 # Configuration
```

## 🔨 Common Commands

```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Run application
mvn spring-boot:run

# Package as JAR
mvn package

# Run packaged JAR
java -jar target/todo-ai-1.0.0.jar
```

## 📝 API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/v1/todo/list` | Get all todos |
| GET | `/v1/todo/list/active` | Get incomplete todos |
| GET | `/v1/todo/list/completed` | Get completed todos |
| GET | `/v1/todo/{id}` | Get specific todo |
| POST | `/v1/todo` | Create new todo |
| PUT | `/v1/todo/{id}` | Update todo |
| DELETE | `/v1/todo/{id}` | Delete todo |

## ✨ Technologies

- Spring Boot 3.2.3
- Java 17
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## 📖 For More Information

- See **README.md** for detailed documentation
- See **DEVELOPMENT.md** for development guidelines
- See **.cursorules** for IDE assistance rules
- See **.claude** for AI context

## 🐛 Troubleshooting

**Port 8080 already in use?**
```bash
# Change port in application.properties
server.port=8081
```

**Build fails?**
```bash
# Clean and rebuild
mvn clean install -U
```

**Tests failing?**
```bash
# Run with skip tests
mvn spring-boot:run -DskipTests
```

Happy coding! 🎉

