# admin-service

Standalone Spring Boot project for Tartheeb.

Purpose: Handles platform administration, configuration, notifications, audit, and reporting read models.

## Technology

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Validation
- Spring Boot Actuator

## Run

```powershell
mvn spring-boot:run
```

The application starts on port `8082`. Epic-01 implements versioned document policies, idempotent centralized audit ingestion, and SMTP password-reset notifications that never persist reset tokens.
