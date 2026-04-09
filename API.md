# API DOCUMENTATION

## Base URL

```
http://localhost:8080/api
```

---

## Authentication

### Register (Public)
```
POST /v1/auth/signup
Content-Type: application/json
```

**Request:**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "SecurePass123!"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "type": "Bearer",
  "expiresIn": 3600000,
  "userId": 1,
  "username": "testuser"
}
```

### Login (Public)
```
POST /v1/auth/login
Content-Type: application/json
```

**Request:**
```json
{
  "username": "testuser",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "type": "Bearer",
  "expiresIn": 3600000,
  "userId": 1,
  "username": "testuser"
}
```

### Get Current User (Protected)
```
GET /v1/auth/me
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "enabled": true
}
```

### Refresh Token (Public)
```
POST /v1/auth/refresh
Authorization: Bearer {refreshToken}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "type": "Bearer",
  "expiresIn": 3600000,
  "userId": 1,
  "username": "testuser"
}
```

---

## Todos

### Get All Todos (Protected)
```
GET /v1/todo/list
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Buy groceries",
    "description": "Milk, eggs, bread",
    "completed": false,
    "reminderEnabled": false,
    "createdAt": "2026-04-01T10:30:00",
    "updatedAt": "2026-04-01T10:30:00"
  }
]
```

### Get Active Todos (Protected)
```
GET /v1/todo/list/active
Authorization: Bearer {token}
```

Returns only incomplete todos.

### Get Completed Todos (Protected)
```
GET /v1/todo/list/completed
Authorization: Bearer {token}
```

Returns only completed todos.

### Get Todo by ID (Protected)
```
GET /v1/todo/{id}
Authorization: Bearer {token}
```

**Example:**
```
GET /v1/todo/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "completed": false,
  "reminderEnabled": false,
  "createdAt": "2026-04-01T10:30:00",
  "updatedAt": "2026-04-01T10:30:00"
}
```

### Create Todo (Protected)
```
POST /v1/todo
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
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
  "reminderEnabled": false,
  "createdAt": "2026-04-01T10:30:00",
  "updatedAt": "2026-04-01T10:30:00"
}
```

### Update Todo (Protected)
```
PUT /v1/todo/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "title": "Buy groceries and cook",
  "description": "Updated description",
  "completed": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Buy groceries and cook",
  "description": "Updated description",
  "completed": true,
  "reminderEnabled": false,
  "createdAt": "2026-04-01T10:30:00",
  "updatedAt": "2026-04-01T11:00:00"
}
```

### Delete Todo (Protected)
```
DELETE /v1/todo/{id}
Authorization: Bearer {token}
```

**Response (204 No Content)**

---

## Reminders

### Get All Reminders (Protected)
```
GET /v1/reminder/list
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "todoId": 1,
    "scheduledTime": "2026-04-10T14:30:00",
    "sent": false,
    "notificationMessage": "Time to buy groceries!",
    "createdAt": "2026-04-01T10:30:00",
    "updatedAt": "2026-04-01T10:30:00"
  }
]
```

### Get Reminders for Todo (Protected)
```
GET /v1/reminder/todo/{todoId}
Authorization: Bearer {token}
```

Returns all reminders for a specific todo.

### Get Reminder by ID (Protected)
```
GET /v1/reminder/{id}
Authorization: Bearer {token}
```

### Create Reminder (Protected)
```
POST /v1/reminder
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "todoId": 1,
  "scheduledTime": "2026-04-10T14:30:00",
  "notificationMessage": "Time to buy groceries!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "todoId": 1,
  "scheduledTime": "2026-04-10T14:30:00",
  "sent": false,
  "notificationMessage": "Time to buy groceries!",
  "createdAt": "2026-04-01T10:30:00",
  "updatedAt": "2026-04-01T10:30:00"
}
```

### Update Reminder (Protected)
```
PUT /v1/reminder/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "scheduledTime": "2026-04-11T14:30:00",
  "notificationMessage": "Updated reminder"
}
```

### Delete Reminder (Protected)
```
DELETE /v1/reminder/{id}
Authorization: Bearer {token}
```

**Response (204 No Content)**

### Trigger Reminder Notification (Protected)
```
POST /v1/reminder/{id}/send
Authorization: Bearer {token}
```

**Response (200 OK):**
```
Notification sent successfully for reminder: 1
```

---

## cURL Examples

### Sign Up
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "email":"test@example.com",
    "password":"SecurePass123!"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "password":"SecurePass123!"
  }'
```

Save the token from response:
```bash
TOKEN="eyJhbGc..."
```

### Get Current User
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/auth/me
```

### Create Todo
```bash
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Buy groceries",
    "description":"Milk, eggs, bread"
  }'
```

### Get All Todos
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/todo/list
```

### Get Active Todos
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/todo/list/active
```

### Update Todo (Mark as Complete)
```bash
curl -X PUT http://localhost:8080/api/v1/todo/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"completed":true}'
```

### Delete Todo
```bash
curl -X DELETE http://localhost:8080/api/v1/todo/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Create Reminder
```bash
curl -X POST http://localhost:8080/api/v1/reminder \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "todoId":1,
    "scheduledTime":"2026-04-10T14:30:00",
    "notificationMessage":"Time to buy groceries!"
  }'
```

### Get All Reminders
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/reminder/list
```

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Invalid request",
  "message": "Title is required"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid credentials"
}
```

### 404 Not Found
```json
{
  "error": "Not found",
  "message": "Todo with id 999 not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred"
}
```

---

## Authentication

All protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer {token}
```

Tokens expire after 1 hour. Use the `/v1/auth/refresh` endpoint with your refresh token to get a new access token.

---

## Rate Limiting

Currently no rate limiting. This will be added in future versions.

---

## Versioning

API version: v1

Future versions will be available at `/api/v2/`, `/api/v3/`, etc.

