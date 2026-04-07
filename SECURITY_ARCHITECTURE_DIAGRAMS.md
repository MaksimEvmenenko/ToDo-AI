# Security Architecture Diagram

## System Architecture Overview

```
┌────────────────────────────────────────────────────────────────────────┐
│                          CLIENT APPLICATIONS                            │
│                    (Web Browser / Mobile / REST Client)                │
└────────────────────────────────────┬─────────────────────────────────┘
                                     │
                    ┌─────────────────┴──────────────────┐
                    │                                    │
                    ▼                                    ▼
        ┌─────────────────────┐          ┌──────────────────────┐
        │   AUTH ENDPOINTS    │          │  PROTECTED ENDPOINTS │
        │  /api/v1/auth/**    │          │   /api/v1/todo/**    │
        │  - login            │          │   /api/v1/reminder/**│
        │  - signup           │          │   /api/v1/health     │
        │  - refresh          │          │   /api/v1/auth/me    │
        └──────────┬──────────┘          └──────────┬───────────┘
                   │                                │
                   │ No Token Required              │ Token Required
                   │                                │
        ┌──────────▼────────────────────────────────▼───────────┐
        │         SPRING SECURITY FILTER CHAIN                  │
        │  ┌────────────────────────────────────────────────┐  │
        │  │ 1. CORS Configuration Filter                   │  │
        │  │    - Allow localhost:3000, localhost:8080      │  │
        │  │    - Allow credentials                         │  │
        │  └────────────────────────────────────────────────┘  │
        │  ┌────────────────────────────────────────────────┐  │
        │  │ 2. JWT Authentication Filter                   │  │
        │  │    - Extract Bearer token from header          │  │
        │  │    - Validate token signature                  │  │
        │  │    - Check token expiration                    │  │
        │  │    - Load user from database                   │  │
        │  │    - Set SecurityContext                       │  │
        │  └────────────────────────────────────────────────┘  │
        │  ┌────────────────────────────────────────────────┐  │
        │  │ 3. Authorization Filter                        │  │
        │  │    - Check if endpoint requires auth           │  │
        │  │    - Validate user roles/permissions           │  │
        │  │    - Return 401 if unauthorized                │  │
        │  └────────────────────────────────────────────────┘  │
        │  ┌────────────────────────────────────────────────┐  │
        │  │ 4. Exception Handler                           │  │
        │  │    - Catch authentication exceptions           │  │
        │  │    - Return 401 JSON response                  │  │
        │  └────────────────────────────────────────────────┘  │
        └──────────┬─────────────────────────────────────┬──────┘
                   │ Valid Token                          │ No/Invalid Token
                   ▼                                      ▼
        ┌─────────────────────┐            ┌────────────────────────┐
        │  AuthController     │            │  Return 401            │
        │  Other Controllers  │            │  Unauthorized          │
        │  Services           │            │  Error Response        │
        │  Repositories       │            │  {"error": "..."}      │
        └─────────────────────┘            └────────────────────────┘
                   │
                   ▼
        ┌─────────────────────────────────────────────────┐
        │         DATABASE LAYER                          │
        │  ┌────────────────────────────────────────────┐ │
        │  │ Users Table                                │ │
        │  │ - id, username, email, password            │ │
        │  │ - enabled, accountNonLocked               │ │
        │  │ - createdAt, updatedAt                    │ │
        │  └────────────────────────────────────────────┘ │
        │  ┌────────────────────────────────────────────┐ │
        │  │ Todos Table (existing)                     │ │
        │  │ - id, title, description, completed        │ │
        │  │ - reminderEnabled, createdAt, updatedAt    │ │
        │  └────────────────────────────────────────────┘ │
        │  ┌────────────────────────────────────────────┐ │
        │  │ Reminders Table (existing)                 │ │
        │  │ - id, todoId, reminderTime, status         │ │
        │  │ - createdAt, updatedAt                    │ │
        │  └────────────────────────────────────────────┘ │
        └─────────────────────────────────────────────────┘
```

---

## JWT Token Flow Diagram

```
USER REGISTRATION/LOGIN
┌─────────────┐
│   Client    │
│  Username + │
│  Password   │
└──────┬──────┘
       │
       │ POST /api/v1/auth/login
       ▼
┌────────────────────────────────────────┐
│     AuthController.login()             │
│  Receives LoginRequest DTO             │
└────────────────┬───────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────┐
│   AuthenticationManager                │
│   Uses DaoAuthenticationProvider       │
└────────────────┬───────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────┐
│  CustomUserDetailsService              │
│  loadUserByUsername(username)          │
└────────────────┬───────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────┐
│   UserRepository.findByUsername()      │
│   Query: SELECT * FROM users           │
│   WHERE username = ?                   │
└────────────────┬───────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────┐
│  PasswordEncoder.matches()             │
│  Verify password against BCrypt hash   │
│  plain_password vs hashed_password     │
└────────────────┬───────────────────────┘
                 │
       ┌─────────┴─────────┐
       │ Valid             │ Invalid
       ▼                   ▼
    SUCCESS            401 UNAUTHORIZED
       │
       ▼
┌────────────────────────────────────────┐
│   JwtTokenProvider                     │
│  .generateAccessToken()                │
│  .generateRefreshToken()               │
└────────────────┬───────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────┐
│  Create JWT Token                      │
│  Header: {alg: HS512, typ: JWT}       │
│  Payload: {sub: username,              │
│            iat: now,                   │
│            exp: now+3600000}           │
│  Signature: HMAC-SHA512(                │
│             header.payload,             │
│             secret_key)                │
└────────────────┬───────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────┐
│  Return AuthResponse                   │
│  {                                     │
│    token: "eyJhbGc...",               │
│    refreshToken: "eyJhbGc...",        │
│    type: "Bearer",                    │
│    expiresIn: 3600000,                │
│    userId: 1,                         │
│    username: "john_doe"               │
│  }                                    │
└────────────────┬───────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │  Client        │
        │  Stores token  │
        │ in localStorage│
        └────────────────┘


ACCESSING PROTECTED RESOURCE
┌─────────────────────────┐
│   Client                │
│  Authorization:         │
│  Bearer {access_token}  │
└──────────────┬──────────┘
               │
               │ GET /api/v1/todo/list
               ▼
┌──────────────────────────────────────────┐
│  JwtAuthenticationFilter                 │
│  1. Extract Bearer token from header     │
│  2. Call JwtTokenProvider.validateToken()│
└──────────────┬───────────────────────────┘
               │
    ┌──────────┴──────────┐
    │ Valid               │ Invalid
    ▼                     ▼
┌─────────────────┐  401 UNAUTHORIZED
│  Parse JWT      │  {"error": "..."}
│  Extract user   │
│  Load from DB   │
│  Set in         │
│  SecurityContext│
└────────┬────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  TodoController.getTodoList()            │
│  Can access SecurityContext.getUser()    │
│  All todos returned                      │
└────────────────┬───────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │  Client        │
        │  Receives      │
        │  Todo List     │
        │  (200 OK)      │
        └────────────────┘


TOKEN REFRESH FLOW
┌──────────────────────────┐
│   Client                 │
│  Authorization:          │
│  Bearer {refresh_token}  │
└──────────────┬───────────┘
               │
               │ POST /api/v1/auth/refresh
               ▼
┌──────────────────────────────────────────┐
│  AuthController.refreshToken()           │
│  1. Extract refresh token                │
│  2. Validate token                       │
│  3. Extract username                     │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  JwtTokenProvider                        │
│  .generateAccessToken()      (new)       │
│  .generateRefreshToken()     (new)       │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  Return New AuthResponse                 │
│  {                                       │
│    token: "eyJhbGc..." (new),           │
│    refreshToken: "eyJhbGc..." (new),    │
│    type: "Bearer",                      │
│    expiresIn: 3600000,                  │
│    userId: 1,                           │
│    username: "john_doe"                 │
│  }                                      │
└──────────────┬───────────────────────────┘
               │
               ▼
        ┌────────────────┐
        │  Client        │
        │  Updates token │
        │  in localStorage
        └────────────────┘
```

---

## Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                     HTTP REQUEST LIFECYCLE                          │
└─────────────────────────────────────────────────────────────────────┘

Client                                                 Server
  │                                                      │
  │ 1. HTTP Request                                     │
  │ (with JWT in Authorization header)                 │
  ├─────────────────────────────────────────────────────>│
  │                                                      │
  │                                    CORS Filter      │
  │                                    (Checks origin)  │
  │                                      │              │
  │                                      ▼              │
  │                         JwtAuthenticationFilter     │
  │                         • Extract token            │
  │                         • Validate signature        │
  │                         • Check expiration          │
  │                         • Load user                 │
  │                         • Set SecurityContext       │
  │                                      │              │
  │                    ┌─────────────────┴──────────┐  │
  │                    │ Token Valid?                │  │
  │                    └─────────────────┬──────────┘  │
  │                                      │              │
  │             ┌────────────────────────┴────────────┐ │
  │             │ NO                      │ YES       │ │
  │             ▼                         ▼           ▼ │
  │    Return 401         Authorization    Controller  │
  │    Unauthorized       Filter          Handler      │
  │                       (Check Auth)       │         │
  │                            │             ▼         │
  │                            │         Business      │
  │                            │         Logic         │
  │                            │             │         │
  │                            └─────────────┤         │
  │                                          │         │
  │              2. HTTP Response            │         │
  │<──────────────────────────────────────────         │
  │ Status Code + Response Body                        │
  │                                                     │
```

---

## Database Schema Relationships

```
┌──────────────────────┐
│      USERS           │
├──────────────────────┤
│ id (PK)              │ ◄──┐
│ username (UNIQUE)    │    │
│ email                │    │
│ password (BCrypt)    │    │
│ enabled              │    │
│ account_non_locked   │    │
│ created_at           │    │
│ updated_at           │    │
└──────────────────────┘    │
                             │
                             │ Future Enhancement:
                             │ Add user_id FK to todos
                             │ and reminders
                             │
         ┌───────────────────┼──────────────────┐
         │                   │                  │
         ▼                   ▼                  ▼
    ┌─────────────┐  ┌─────────────┐  ┌──────────────┐
    │   TODOS     │  │ REMINDERS   │  │ AUDIT_LOGS   │
    ├─────────────┤  ├─────────────┤  ├──────────────┤
    │ id (PK)     │  │ id (PK)     │  │ id (PK)      │
    │ title       │  │ reminder... │  │ user_id (FK) │
    │ description │  │ status      │  │ action       │
    │ completed   │  │ created_at  │  │ timestamp    │
    │ reminder... │  │ updated_at  │  └──────────────┘
    │ created_at  │  └─────────────┘
    │ updated_at  │
    └─────────────┘

Legend:
PK = Primary Key
FK = Foreign Key (Future)
UNIQUE = Unique constraint
```

---

## Security Layers

```
┌────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                        │
│  HTTP Controllers (public, no sensitive data)          │
└────────────────────────┬───────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         │                               │
         ▼                               ▼
    ┌─────────────┐            ┌─────────────────┐
    │ PUBLIC      │            │ REQUIRES AUTH   │
    │ Endpoints   │            │ Endpoints       │
    ├─────────────┤            ├─────────────────┤
    │ /auth/*     │            │ /todo/*         │
    │ /health     │            │ /reminder/*     │
    │ /api-docs   │            │ /auth/me        │
    └─────────────┘            └─────────────────┘
         │                            │
         ▼                            ▼
    ┌────────────────────────────────────────────┐
    │      SECURITY LAYER                        │
    │  • Spring Security Filters                 │
    │  • JWT Validation                          │
    │  • Authorization Checks                    │
    └────────────────┬───────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
         ▼                       ▼
    ┌─────────────┐      ┌──────────────┐
    │ VALID TOKEN │      │ INVALID TOKEN│
    │ Proceed     │      │ Return 401   │
    │ Set User in │      │ Stop request │
    │ Context     │      └──────────────┘
    └──────┬──────┘
           │
           ▼
    ┌────────────────────────────────────────────┐
    │      SERVICE LAYER                         │
    │  • Business Logic                          │
    │  • Data Validation                         │
    │  • Password Hashing (BCrypt)               │
    └────────────────┬───────────────────────────┘
                     │
                     ▼
    ┌────────────────────────────────────────────┐
    │      DATA ACCESS LAYER                     │
    │  • Spring Data JPA Repositories            │
    │  • Parameterized Queries (SQL Injection    │
    │    Protection)                             │
    └────────────────┬───────────────────────────┘
                     │
                     ▼
    ┌────────────────────────────────────────────┐
    │      DATABASE LAYER                        │
    │  • MySQL/PostgreSQL/SQLite                 │
    │  • Encrypted Password Storage              │
    │  • Connection Pooling                      │
    │  • Transaction Management                  │
    └────────────────────────────────────────────┘
```

---

## Token Lifecycle

```
TOKEN CREATION
┌────────────────────────────────────┐
│ User logs in successfully           │
│ Password matches BCrypt hash        │
└──────────────┬─────────────────────┘
               │
               ▼
   ┌──────────────────────┐
   │ Generate Access      │
   │ Token                │
   │ Expiry: NOW + 1hr    │
   └──────────┬───────────┘
              │
              ▼
   ┌──────────────────────┐
   │ Generate Refresh     │
   │ Token                │
   │ Expiry: NOW + 24hr   │
   └──────────┬───────────┘
              │
              ▼
    ┌────────────────────┐
    │ Send to Client     │
    │ Store in Browser   │
    │ localStorage       │
    └────────┬───────────┘
             │
             ▼
TOKEN IN USE
   ┌────────────────────────────────┐
   │ Client includes Access Token   │
   │ in every API request header    │
   │ Authorization: Bearer <token>  │
   └──────────────┬─────────────────┘
                  │
         ┌────────┴────────┐
         │                 │
         ▼                 ▼
    TOKEN VALID        TOKEN EXPIRED
    Request proceeds   Refresh flow
    │                  │
    │                  ├─ Send Refresh Token
    │                  ├─ Get New Access Token
    │                  └─ Retry original request
    │                  
    ▼
 RESPONSE

TOKEN EXPIRATION
    ┌────────────────────────────────┐
    │ Access Token Expires           │
    │ (after 1 hour)                 │
    └──────────────┬─────────────────┘
                   │
         ┌─────────┴────────┐
         │                  │
         ▼                  ▼
    REFRESH TOKEN    REFRESH TOKEN
    Still Valid      Expired
    │                │
    ├─ Can refresh   └─ User must
    │  access token     login again
    │
    └─ Get new
       tokens
```

---

## Password Security Process

```
USER ENTERS PASSWORD
"SecurePass123"
       │
       ▼
┌──────────────────────┐
│ BCryptPasswordEncoder│
│ • Generate random   │
│   salt              │
│ • Hash password 10  │
│   rounds            │
└──────────┬───────────┘
           │
           ▼
HASHED PASSWORD
"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm"
(60 characters, always different for same password)
       │
       ▼
┌──────────────────────┐
│ Store in Database    │
│ (NEVER store plain)  │
└──────────┬───────────┘
           │
           ▼
NEXT LOGIN
User enters password: "SecurePass123"
       │
       ▼
┌──────────────────────────────┐
│ PasswordEncoder.matches()    │
│ • Hash new input              │
│ • Compare with stored hash    │
│ • Using timing-resistant      │
│   comparison                  │
└──────────┬───────────────────┘
           │
    ┌──────┴──────┐
    │ MATCH       │ NO MATCH
    ▼             ▼
  LOGIN        LOGIN DENIED
  SUCCESS      (401)
```


