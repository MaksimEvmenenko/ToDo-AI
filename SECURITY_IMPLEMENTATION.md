# Spring Security & JWT Implementation Guide

## Overview
This document describes the security layer implementation for the ToDo-AI application using Spring Security and JWT (JSON Web Tokens).

## What Was Implemented

### 1. **Dependencies Added** (build.gradle)
```groovy
- org.springframework.boot:spring-boot-starter-security
- io.jsonwebtoken:jjwt-api:0.12.3
- io.jsonwebtoken:jjwt-impl:0.12.3
- io.jsonwebtoken:jjwt-jackson:0.12.3
- org.springframework.security:spring-security-test
```

### 2. **JWT Configuration** (application.properties)
```properties
jwt.secret=your-secret-key-change-this-in-production-min-32-characters-required
jwt.expiration=3600000 (1 hour in ms)
jwt.refresh-expiration=86400000 (24 hours in ms)
```

## Architecture

### User Model (`User.java`)
- Stores user credentials and account status
- Fields: id, username, email, password, enabled, accountNonLocked, createdAt, updatedAt
- Database table: users with unique constraint on username

### Security Components

#### 1. **JwtTokenProvider** (`security/JwtTokenProvider.java`)
Responsible for:
- Generating access tokens (1 hour expiry)
- Generating refresh tokens (24 hours expiry)
- Validating tokens
- Extracting username from tokens
- Using HMAC SHA-512 algorithm for signing

Key Methods:
- `generateAccessToken(Authentication)` - Create access token from authentication
- `generateAccessToken(String username)` - Create access token from username
- `generateRefreshToken(String username)` - Create refresh token
- `validateToken(String token)` - Validate token signature and expiry
- `getUsernameFromJwt(String token)` - Extract username from token

#### 2. **JwtAuthenticationFilter** (`security/JwtAuthenticationFilter.java`)
- Extends OncePerRequestFilter
- Intercepts every request
- Extracts JWT from Authorization header (Bearer token)
- Validates token and sets authentication in SecurityContextHolder
- Enables stateless authentication

#### 3. **CustomUserDetailsService** (`security/CustomUserDetailsService.java`)
- Implements Spring's UserDetailsService interface
- Loads user from database by username
- Returns Spring UserDetails object with user's credentials and authorities
- Grants ROLE_USER to all authenticated users

#### 4. **SecurityConfig** (`config/SecurityConfig.java`)
Configures:
- **PasswordEncoder**: BCryptPasswordEncoder (industry standard)
- **AuthenticationManager**: Uses DaoAuthenticationProvider
- **Filter Chain**:
  - Permits: `/api/v1/auth/**` (login, signup, refresh)
  - Permits: `/api/v1/health`
  - Requires authentication: all other endpoints
  - Stateless sessions (no cookies)
  - JWT filter before UsernamePasswordAuthenticationFilter
- **CORS**: Allows localhost:3000 and localhost:8080
- **CSRF**: Disabled (stateless API doesn't need it)

### Authentication Endpoints

#### 1. **POST /api/v1/auth/login**
Request:
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresIn": 3600000,
  "userId": 1,
  "username": "john_doe"
}
```

#### 2. **POST /api/v1/auth/signup**
Request:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

Response: Same as login

#### 3. **POST /api/v1/auth/refresh**
Header:
```
Authorization: Bearer {refresh_token}
```

Response: New tokens

#### 4. **GET /api/v1/auth/me**
Header:
```
Authorization: Bearer {access_token}
```

Returns authenticated user info

## Usage

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

### 3. Access Protected Endpoints
```bash
curl -H "Authorization: Bearer <your_jwt_token>" \
  http://localhost:8080/api/v1/todo/list
```

### 4. Refresh Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Authorization: Bearer <your_refresh_token>"
```

## Best Practices Implemented

✅ **Password Encryption**: Uses BCryptPasswordEncoder with salt
✅ **JWT Signing**: HMAC SHA-512 algorithm
✅ **Token Validation**: Validates signature, expiration, and format
✅ **Stateless Authentication**: No session storage required
✅ **CORS Support**: Allows frontend from localhost
✅ **Refresh Tokens**: Separate tokens for long-lived sessions
✅ **Error Handling**: Proper HTTP status codes (401, 400, 500)
✅ **Logging**: Debug logs for authentication flow
✅ **Validation**: Input validation using Jakarta annotations
✅ **Separation of Concerns**: Filter, Service, and Config layers

## Files Created/Modified

### Created:
- `src/main/java/com/todoai/model/User.java`
- `src/main/java/com/todoai/dto/LoginRequest.java`
- `src/main/java/com/todoai/dto/SignUpRequest.java`
- `src/main/java/com/todoai/dto/AuthResponse.java`
- `src/main/java/com/todoai/repository/UserRepository.java`
- `src/main/java/com/todoai/security/JwtTokenProvider.java`
- `src/main/java/com/todoai/security/JwtAuthenticationFilter.java`
- `src/main/java/com/todoai/security/CustomUserDetailsService.java`
- `src/main/java/com/todoai/config/SecurityConfig.java`
- `src/main/java/com/todoai/controller/AuthController.java`

### Modified:
- `build.gradle` - Added Spring Security and JWT dependencies
- `src/main/resources/application.properties` - Added JWT configuration

## Security Considerations

### Production Deployment Checklist:
- [ ] Change `jwt.secret` to a strong 32+ character key
- [ ] Use HTTPS for all endpoints
- [ ] Set appropriate `jwt.expiration` times
- [ ] Update CORS allowed origins for your domain
- [ ] Implement rate limiting on auth endpoints
- [ ] Add request logging and monitoring
- [ ] Use environment variables for JWT secret
- [ ] Implement user account lockout after failed attempts
- [ ] Add refresh token rotation mechanism
- [ ] Implement logout/token blacklist system

## Testing Endpoints

### Health Check (No Auth Required)
```bash
curl http://localhost:8080/api/health
```

### Protected Endpoints (Require Token)
```bash
# Get all todos (requires auth)
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/todo/list

# Create todo (requires auth)
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Sample Todo",
    "description": "A sample todo item",
    "completed": false,
    "reminderEnabled": false
  }'
```

## Next Steps

1. **Database Migration**: Create users table in your database
2. **Build Project**: Run `./gradlew clean build`
3. **Start Application**: Run `./gradlew bootRun`
4. **Test API**: Use the curl commands above
5. **Frontend Integration**: Implement token storage (localStorage/sessionStorage)
6. **Token Refresh**: Implement automatic token refresh logic in frontend
7. **Error Handling**: Handle 401 responses with logout redirection

## Troubleshooting

### "Cannot resolve symbol 'security'"
- Run `./gradlew clean` to refresh dependencies
- Invalidate IDE cache and restart
- Check that build.gradle has spring-boot-starter-security

### Token validation fails
- Verify `jwt.secret` length is at least 32 characters
- Check token hasn't expired (expiresIn = 3600000 ms = 1 hour)
- Ensure Authorization header format is: `Bearer <token>`

### Login returns 401
- Verify user exists in database
- Check password is correct (case-sensitive)
- Ensure password encoding matches (BCrypt)


