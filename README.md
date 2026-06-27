# CurrencyXchange API — Spring Boot

An industry-level RESTful API for real-time currency conversion, built with Spring Boot 3. 
Demonstrates production-quality backend practices including clean architecture, security, API documentation, and robust error handling.

## 🚀 Features

- **Real-time Conversion**: Live exchange rates via [ExchangeRate-API](https://www.exchangerate-api.com/).
- **Secure**: HTTP Basic Authentication with BCrypt password hashing.
- **Conversion History**: Persists all conversions per user in a relational database.
- **Validation**: Strict input validation using Jakarta Validation (`@Valid`, `@Positive`).
- **Standardized Errors**: Global `@RestControllerAdvice` for consistent JSON error responses.
- **API Documentation**: Auto-generated Swagger/OpenAPI UI.
- **Frontend Dashboard**: Beautiful glassmorphism UI built with Vanilla JS (Fetch API).

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3 (Web, Data JPA, Security)
- **Database**: H2 (Development) / PostgreSQL (Production ready via profiles)
- **Security**: Spring Security (Basic Auth + BCrypt)
- **Documentation**: Springdoc OpenAPI (Swagger UI)
- **Frontend**: HTML5, CSS3 (Glassmorphism), Vanilla JavaScript

## 📁 Architecture

The project follows a classic Layered Architecture:

1. **Controller Layer**: Handles HTTP requests, DTO mapping, and input validation.
2. **Service Layer**: Business logic, external API calls, and transaction management.
3. **Repository Layer**: Database access via Spring Data JPA.
4. **DTOs**: Ensures database entities (`@Entity`) are never exposed directly to the client.

## ⚙️ Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/jayadevharees5-creator/currency-converter.git
   cd currency-converter
   ```

2. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - Frontend Dashboard: `http://localhost:8080/`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Database Console: `http://localhost:8080/h2-console`

## 🔑 Authentication

Most endpoints are protected via HTTP Basic Authentication.
Upon startup, a default test user is created:
- **Username**: `testuser`
- **Password**: `password123`

## 📡 API Endpoints

### Public Endpoints
- `GET /` — Application metadata and health check.
- `GET /swagger-ui.html` — API documentation.

### Protected Endpoints (`/api/**`)
- `GET /api/currencies` — List all supported currency codes.
- `POST /api/convert` — Perform a currency conversion.
- `GET /api/convert` — Perform a currency conversion (legacy query param support).
- `GET /api/history` — Get conversion history for the authenticated user.

## 📈 Future Enhancements

- **JWT Authentication**: Migrate from Basic Auth to stateless JWT tokens.
- **Caching**: Implement `@Cacheable` for exchange rates to reduce external API calls.
- **Role-Based Access Control (RBAC)**: Admin endpoints to view all users' history.
