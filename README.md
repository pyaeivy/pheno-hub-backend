# PhenoHub

PhenoHub is a Spring Boot backend service for managing phone catalog data — brands, models, specifications, images, and user profiles. It provides JWT-based authentication, role-based access control, and a generic criteria-based search layer for querying phone data.

## Tech Stack

- **Java 21**
- **Spring Boot 4.1.1** (packaged as a WAR)
- **Spring Data JPA** with **PostgreSQL**
- **Spring Security** with **JWT** (jjwt 0.11.2)
- **Spring Cache** with **Caffeine**
- **Lombok** for boilerplate reduction
- **Spring AI BOM** (2.0.1) declared for future AI features

## Project Structure

```
src/main/java/com/example/demo/
├── controller/        # REST controllers (Auth, User, Phone)
├── service/           # Business logic (AuthService, UserService, PhoneService)
├── repository/        # JPA repositories + generic criteria search
├── entity/            # JPA entities (User, PhoneModel, PhoneBrand, etc.)
├── dto/               # Request/response DTOs
├── security/          # JWT utilities, auth filter, security config
├── exception/         # Custom exceptions and handlers
├── dao/               # Data access helpers
└── implementation/    # Implementation classes
```

## Domain Model

- **User** — registered users with roles (`Role` entity)
- **PhoneBrand** — phone manufacturers
- **PhoneModel** — specific phone models belonging to a brand
- **PhoneSpecification** — technical specs for a model
- **PhoneImage** — images categorized by `ImageCategory`
- **PhoneAccessories** — accessories linked to models
- **Currency** — supported currencies

## API Endpoints

### Auth — `/pheno/auth`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/register` | Register a new user |
| POST | `/login` | Login; returns access token + sets refresh cookie |
| POST | `/logout` | Clear auth cookies |
| POST | `/refresh` | Exchange refresh token for a new access token |

### User — `/pheno/user`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/{phone}` | Find user by phone number |
| GET | `/username/{username}` | Find user by username |
| GET | `/email/{email}` | Find user by email |
| POST | `/update/{username}` | Update user info |

### Phone — `/pheno/phone`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/add`, `/addPhone` (+ `/{username}` variants) | Add a phone |
| POST | `/addPhoneSpec/{modelId}` | Add a specification to a model |
| POST | `/image`, `/createImage` (multipart) | Upload a phone image |
| GET | `/username/{username}`, `/phonelist/{username}` | List phones by username |
| GET | `/selected/{username}` | Get the user's selected phone |
| POST | `/select/{username}/{modelId}` | Select a phone for a user |

## Configuration

Configured in `src/main/resources/application.properties`:

- **Database**: PostgreSQL at `jdbc:postgresql://localhost:5432/phenohub`
- **JPA**: `ddl-auto=update` (schema auto-generated)
- **JWT secret**: set via `jwt.secret` property

## Getting Started

### Prerequisites

- Java 21
- Maven (or use the included `mvnw` wrapper)
- PostgreSQL running locally with a `phenohub` database

### Build & Run

```bash
# Build
./mvnw clean package

# Run (from project root)
./mvnw spring-boot:run
```

The app runs as a WAR — it can also be deployed to an external servlet container (see `ServletInitializer`).

## Authentication Flow

1. `POST /pheno/auth/register` to create an account.
2. `POST /pheno/auth/login` returns an access token (JWT) and sets a refresh token cookie (7 days).
3. Access tokens expire after ~15 minutes; call `POST /pheno/auth/refresh` to get a new one.
4. `JwtAuthFilter` validates the token on each request; `SecurityConfig` enforces role-based rules.

## Notes

- Password strength is validated via `PasswordStrength`.
- A generic criteria API (`SearchCriteria`, `Operator`, `LogicalOperator`, `GenericCriteriaRepository`) supports dynamic filtering on phone queries.
- Image uploads use `multipart/form-data` with a `category` parameter of type `ImageCategory`.
