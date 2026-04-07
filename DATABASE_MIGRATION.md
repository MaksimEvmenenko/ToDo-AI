# Database Migration for User Management

## Overview
This document provides SQL scripts to create the users table for the authentication system.

## SQL Scripts

### Create Users Table

#### For MySQL/MariaDB
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### For PostgreSQL
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_username UNIQUE (username)
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
```

#### For H2 (Development/Testing)
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
```

#### For SQLite
```sql
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT 1,
    account_non_locked BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_email ON users(email);
```

---

## Table Schema

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | User unique identifier |
| username | VARCHAR(100) | NOT NULL, UNIQUE | Unique username for login |
| email | VARCHAR(255) | NOT NULL | User email address |
| password | VARCHAR(255) | NOT NULL | BCrypt encoded password |
| enabled | BOOLEAN | NOT NULL, DEFAULT true | Account enabled status |
| account_non_locked | BOOLEAN | NOT NULL, DEFAULT true | Account lock status |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

---

## Spring Data JPA Automatic Table Creation

Alternatively, let Spring Data JPA create the table automatically:

### application.properties
```properties
# Enable automatic table creation
spring.jpa.hibernate.ddl-auto=create-drop    # For development
spring.jpa.hibernate.ddl-auto=update         # For production

# Show SQL statements in logs
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### application.yml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update    # or create-drop for dev
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## Using Flyway for Database Migrations

For production environments, use Flyway for version-controlled migrations.

### 1. Add Flyway Dependency
```gradle
implementation 'org.flywaydb:flyway-core:9.20.2'
```

### 2. Create Migration File
**File**: `src/main/resources/db/migration/V1__Create_Users_Table.sql`

```sql
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_username UNIQUE (username)
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
```

### 3. Configure Flyway
```properties
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

---

## Sample Data for Testing

### Insert Test Users
```sql
-- Note: Password = "test123" hashed with BCrypt
-- You would use an encoder to generate actual hashes

INSERT INTO users (username, email, password, enabled, account_non_locked) VALUES
('john_doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', true, true),
('jane_smith', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', true, true),
('admin_user', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', true, true);
```

**Note**: To generate BCrypt hashes for testing, use:
```java
// In a test class
String hashedPassword = new BCryptPasswordEncoder().encode("test123");
System.out.println(hashedPassword);
```

---

## Verification

### Check Table Creation
```sql
-- MySQL
SHOW TABLES;
DESCRIBE users;

-- PostgreSQL
\dt users;
\d users;

-- SQLite
.tables
.schema users
```

### Check Sample Data
```sql
SELECT * FROM users;
SELECT COUNT(*) FROM users;
```

---

## Common Issues

### Issue: "Table already exists"
**Solution**: Drop the table first
```sql
DROP TABLE IF EXISTS users;
-- Then run CREATE TABLE script
```

### Issue: "Column 'password' is too short"
**Solution**: BCrypt hashes are 60 characters, ensure VARCHAR(255) or larger
```sql
ALTER TABLE users MODIFY COLUMN password VARCHAR(255);
```

### Issue: "Duplicate entry for key 'username'"
**Solution**: Username already exists, use UPDATE instead of INSERT
```sql
UPDATE users SET email = 'newemail@example.com' WHERE username = 'john_doe';
```

### Issue: "Cannot add or drop FOREIGN KEY constraint"
**Solution**: No foreign keys defined for users table, check references
```sql
-- Check existing constraints
SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_NAME = 'users';
```

---

## Backup and Recovery

### Backup Users Table
```sql
-- MySQL
CREATE TABLE users_backup AS SELECT * FROM users;

-- PostgreSQL
CREATE TABLE users_backup AS SELECT * FROM users;

-- SQLite
CREATE TABLE users_backup AS SELECT * FROM users;
```

### Restore from Backup
```sql
-- Clear current table
DELETE FROM users;

-- Restore from backup
INSERT INTO users SELECT * FROM users_backup;

-- Drop backup
DROP TABLE users_backup;
```

---

## Performance Optimization

### Create Indexes for Common Queries
```sql
-- Already created in table schema, but can add more:
CREATE INDEX idx_created_at ON users(created_at);
CREATE INDEX idx_enabled ON users(enabled);
CREATE INDEX idx_account_non_locked ON users(account_non_locked);
```

### Query Performance
```sql
-- Fast lookup by username
SELECT * FROM users WHERE username = 'john_doe';  -- Uses idx_username

-- Fast lookup by email
SELECT * FROM users WHERE email = 'john@example.com';  -- Uses idx_email

-- List active users
SELECT * FROM users WHERE enabled = true;  -- Consider adding index
```

---

## Spring Boot Properties for Your Project

Update `application.properties`:

```properties
# For Development (Auto-create tables)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# For Production (Manual migrations)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Datasource (adjust to your setup)
spring.datasource.url=jdbc:mysql://localhost:3306/todoai
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

---

## Verification Script

Run this to verify setup:

```bash
# MySQL
mysql -u root -p todoai < /path/to/create_users_table.sql

# PostgreSQL
psql -U postgres -d todoai -f /path/to/create_users_table.sql

# Then start application
./gradlew bootRun

# Check logs for "Creating tables" if using ddl-auto=create
```

---

## Next Steps

1. ✅ Choose your database (MySQL/PostgreSQL/SQLite/H2)
2. ✅ Run the appropriate SQL script above
3. ✅ Update `application.properties` with datasource config
4. ✅ Start the application with `./gradlew bootRun`
5. ✅ Test the `/api/v1/auth/signup` endpoint
6. ✅ Verify user is created in database


