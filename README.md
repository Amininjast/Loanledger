# LoanLedger

**Clean Loan & Installment Management System**

A production-style backend project demonstrating:

- Layered Architecture (Controller → Service → Repository)
- SOLID principles
- Clean Code & Domain-Driven design elements
- ACID transactions with proper isolation for concurrent payments
- JWT Authentication & Role-based Authorization
- Spring Boot 3 + Java 21 best practices

---

## Big Picture (Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                        Client / Swagger                     │
└────────────────────────────┬────────────────────────────────┘
                             │ REST (JSON)
┌────────────────────────────▼────────────────────────────────┐
│  Controllers (auth, user, loan, payment)                    │
│  + DTO validation (Bean Validation)                         │
│  + Global Exception Handler → ApiResponse                   │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│  Services (interfaces + implementations)                    │
│  - Business rules & invariants                              │
│  - @Transactional (especially PaymentService)               │
│  - Strategy / calculation logic                             │
│  - Audit logging                                            │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│  Repositories (Spring Data JPA)                             │
│  - Pessimistic locking for payment concurrency              │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│  PostgreSQL  (Flyway migrations)                            │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure (final – do not reorganize)

```
com.loanledger
├── auth          → login / register / JWT
├── user          → user management
├── loan          → loan aggregate root
├── installment   → part of loan (composition)
├── payment       → payment transactions (ACID critical)
├── audit         → audit trail
├── common        → exceptions, ApiResponse, utils
└── config        → Security, OpenAPI, etc.
```

### Core Domain Model

| Aggregate / Entity | Responsibility                              |
|--------------------|---------------------------------------------|
| **User**           | Identity + roles                            |
| **Loan**           | Aggregate root. Owns Installments           |
| **Installment**    | Single scheduled payment (Composition)      |
| **Payment**        | Money movement against an Installment       |
| **AuditLog**       | Immutable record of important actions       |

**Key Business Rules:**
- Loan can only be activated when it has installments
- Payment never overpays an installment
- Concurrent payments on the same installment are serialized (Pessimistic Lock + Transaction)
- Loan status transitions are controlled

---

## Tech Stack

| Technology            | Purpose                          |
|-----------------------|----------------------------------|
| Java 21               | Language                         |
| Spring Boot 3.3       | Application framework            |
| Spring Web            | REST API                         |
| Spring Data JPA       | Persistence                      |
| Hibernate             | ORM                              |
| PostgreSQL 16         | Database                         |
| Flyway                | Schema migrations                |
| Spring Security + JWT | Authentication / Authorization   |
| Bean Validation       | Request validation               |
| springdoc-openapi     | Swagger UI                       |
| MapStruct             | DTO ↔ Entity mapping             |
| JUnit 5 + Mockito     | Unit tests                       |
| Testcontainers        | Integration tests                |
| Docker Compose        | Local PostgreSQL                 |
| Maven                 | Build                            |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### Run locally

```bash
# Start database
docker compose up -d

# Run the application
./mvnw spring-boot:run
# or: mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs:  http://localhost:8080/api-docs

---

## Development Roadmap (commit-friendly)

### Phase 0 – Foundation ✅
- [x] Project skeleton & final package structure
- [x] pom.xml with all dependencies
- [x] Docker Compose + application.yml
- [x] BaseEntity, Enums, core domain entities
- [x] Repositories + Flyway V1 schema
- [x] Common response & exception handling skeleton

### Phase 1 – Domain Services & Business Logic
- [ ] LoanService (create, activate, generate installments)
- [ ] Installment generation strategy
- [ ] PaymentService with `@Transactional` + pessimistic lock
- [ ] AuditService
- [ ] Business exceptions for all rule violations

### Phase 2 – REST API Layer
- [ ] DTOs + MapStruct mappers
- [ ] Controllers (Loan, Payment, User)
- [ ] Request validation
- [ ] Pagination & filtering

### Phase 3 – Security
- [ ] JWT utility + filter
- [ ] SecurityConfig (stateless)
- [ ] Register / Login endpoints
- [ ] Role-based access (`ROLE_ADMIN`, `ROLE_CUSTOMER`)

### Phase 4 – Testing & Polish
- [ ] Unit tests for services (Mockito)
- [ ] Integration tests (Testcontainers)
- [ ] Actuator health
- [ ] Professional README + screenshots

---

## Design Decisions (stable)

1. **Loan is the Aggregate Root** → Installments are never managed outside the Loan context.
2. **Payment is a separate transaction entity** → allows full audit of money movement and easy concurrency control.
3. **Pessimistic locking on Installment** → demonstrates real ACID / Isolation handling.
4. **BaseEntity + UUID** → consistent identity and audit timestamps.
5. **ApiResponse + GlobalExceptionHandler** → uniform public API contract.
6. **Flyway from day one** → schema is versioned and reproducible.
7. **Packages by domain** → scales better and matches bounded contexts.
