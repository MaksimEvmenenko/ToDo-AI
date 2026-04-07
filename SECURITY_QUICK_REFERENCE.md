# Security Implementation - Quick Reference

## JWT Token Structure

```
Header.Payload.Signature

Example:
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNjEyMzQ1NjAwLCJleHAiOjE2MTIzNDkzMDB9.signature
```

## Authentication Flow

```
1. User sends credentials (username + password)
           ↓
2. AuthController receives LoginRequest
           ↓
3. AuthenticationManager validates credentials against User in database
           ↓
4. If valid: JwtTokenProvider generates access + refresh tokens
           ↓
5. AuthResponse returned with tokens
           ↓
6. Client stores tokens and sends access token in Authorization header
           ↓
7. JwtAuthenticationFilter intercepts request
           ↓
8. Token validated and user set in SecurityContext
           ↓
9. Request proceeds to controller
```

## API Endpoints Summary

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| POST | `/api/v1/auth/login` | ❌ No | User login |
| POST | `/api/v1/auth/signup` | ❌ No | User registration |
| POST | `/api/v1/auth/refresh` | ✅ Yes | Refresh access token |
| GET | `/api/v1/auth/me` | ✅ Yes | Get current user |
| GET | `/api/v1/health` | ❌ No | Health check |
| GET | `/api/v1/todo/list` | ✅ Yes | Get all todos |
| POST | `/api/v1/todo` | ✅ Yes | Create todo |
| PUT | `/api/v1/todo/{id}` | ✅ Yes | Update todo |
| DELETE | `/api/v1/todo/{id}` | ✅ Yes | Delete todo |

## Configuration Properties

```properties
# JWT Token Expiration Times
jwt.expiration=3600000              # Access token: 1 hour
jwt.refresh-expiration=86400000     # Refresh token: 24 hours

# JWT Secret (CHANGE IN PRODUCTION!)
jwt.secret=your-secret-key-change-this-in-production-min-32-characters-required

# CORS Configuration (in SecurityConfig)
allowedOrigins: localhost:3000, localhost:8080

# Session Policy
SessionCreationPolicy: STATELESS (no cookies)
```

## Key Classes

### User Model
- **Table**: `users`
- **Fields**: id, username, email, password, enabled, accountNonLocked
- **Unique Constraints**: username

### DTOs
- **LoginRequest**: username, password
- **SignUpRequest**: username, email, password
- **AuthResponse**: token, refreshToken, type, expiresIn, userId, username

### Security Classes
- **JwtTokenProvider**: Token generation/validation
- **JwtAuthenticationFilter**: Request interceptor for token validation
- **CustomUserDetailsService**: Load user from database
- **SecurityConfig**: Spring Security configuration

## Password Security

All passwords are encoded using **BCryptPasswordEncoder**:
- Unique salt per password
- 10 rounds of hashing
- Cannot be reversed (one-way encryption)
- Example: plain `password123` → hashed `$2a$10$...`

## Common HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | Success | Login successful, token returned |
| 201 | Created | User signup successful |
| 400 | Bad Request | Username already exists, invalid email |
| 401 | Unauthorized | Invalid credentials, expired token |
| 403 | Forbidden | Token valid but insufficient permissions |
| 500 | Server Error | Database error, signup failure |

## JWT Token Lifetime

```
Access Token:
├─ Lifetime: 1 hour (3600000 ms)
├─ Use Case: Accessing protected resources
├─ Storage: localStorage (client-side)
└─ Refresh: Use refresh token to get new access token

Refresh Token:
├─ Lifetime: 24 hours (86400000 ms)
├─ Use Case: Getting new access tokens
├─ Storage: localStorage or secure httpOnly cookie
└─ Security: Should be rotated periodically
```

## Implementation Checklist

- ✅ User model with password field
- ✅ User repository with find by username/email
- ✅ JWT token provider with sign/verify methods
- ✅ JWT authentication filter
- ✅ Custom user details service
- ✅ Security configuration with filter chain
- ✅ Authentication controller (login/signup/refresh)
- ✅ Password encoding with BCrypt
- ✅ CORS configuration
- ✅ Error handling for auth failures
- ✅ Input validation on DTOs
- ✅ Logging for security events

## Frontend Integration Example

```javascript
// Login
const response = await fetch('http://localhost:8080/api/v1/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'john', password: 'pwd123' })
});
const data = await response.json();
localStorage.setItem('accessToken', data.token);

// Using protected endpoint
const todos = await fetch('http://localhost:8080/api/v1/todo/list', {
  headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
});

// Refresh token
const newTokens = await fetch('http://localhost:8080/api/v1/auth/refresh', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${localStorage.getItem('refreshToken')}` }
});
```

## Monitoring & Debugging

Enable debug logging:
```properties
logging.level.com.todoai=DEBUG
logging.level.org.springframework.security=DEBUG
```

Look for logs:
- "Set Spring Security authentication for user: {username}"
- "Invalid JWT signature"
- "Expired JWT token"
- "User not found: {username}"
- "User logged in successfully"


