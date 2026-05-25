# Personal Finance Manager API

Spring Boot 3 / Java 17 backend for the Personal Finance Manager assignment. It supports session-based authentication, transaction CRUD, default and custom categories, savings goals, and monthly/yearly reports.

## Tech Stack

- Java 17
- Spring Boot 3.3
- Spring Security with HTTP session cookies
- Spring Data JPA
- H2 database
- Maven
- JUnit 5, Mockito, JaCoCo

## Run Locally

Install Java 17 and Maven, then run:

```bash
mvn test
mvn spring-boot:run
```

The API starts at:

```text
http://localhost:8080/api
```

By default the app uses an in-memory H2 database, which is ideal for the assignment test script because every run starts clean. For file persistence, set `DATABASE_URL=jdbc:h2:file:./data/finance-db`.

## Authentication Flow

The app uses cookies. Save cookies from login and reuse them for protected endpoints.

```bash
curl -i -c cookies.txt -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123","fullName":"John Doe","phoneNumber":"+1234567890"}'

curl -i -c cookies.txt -b cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'
```

## API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`

### Categories

- `GET /api/categories`
- `POST /api/categories`
- `DELETE /api/categories/{name}`

Default categories are created on startup:

- Income: `Salary`
- Expense: `Food`, `Rent`, `Transportation`, `Entertainment`, `Healthcare`, `Utilities`

### Transactions

- `POST /api/transactions`
- `GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31&categoryId=1&type=INCOME`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`

### Savings Goals

- `POST /api/goals`
- `GET /api/goals`
- `GET /api/goals/{id}`
- `PUT /api/goals/{id}`
- `DELETE /api/goals/{id}`

### Reports

- `GET /api/reports/monthly/{year}/{month}`
- `GET /api/reports/yearly/{year}`

## Test Report

Run:

```bash
mvn clean test
```

JaCoCo report:

```text
target/site/jacoco/index.html
```

Assignment script:

```bash
bash financial_manager_tests.sh http://localhost:8080/api
```

For Render:

```bash
bash financial_manager_tests.sh https://your-render-service.onrender.com/api
```

Take a screenshot of the terminal after the script shows the final summary.

## Deploy on Render

Fastest path:

1. Push this repository to GitHub.
2. Go to Render and create a new `Web Service`.
3. Connect the GitHub repository.
4. Choose Docker environment.
5. Keep the free plan.
6. Deploy.

Render reads `Dockerfile` and starts the app on `PORT=8080`. Health check is available at:

```text
/health
```

## One-Day Completion Plan

1. First 2 hours: verify the local API with `mvn test` and a few `curl` calls.
2. Next 1 hour: create a public GitHub repo and push the code.
3. Next 1 hour: deploy on Render using Docker.
4. Next 2 hours: run the provided `financial_manager_tests.sh` against the Render URL and fix any endpoint mismatches.
5. Final 30 minutes: capture the successful test screenshot and submit GitHub URL, live API URL, and screenshot.

## Design Notes

- Controllers only handle HTTP mapping.
- Services contain validation, ownership checks, and finance calculations.
- Repositories isolate database access.
- DTO records keep request/response payloads separate from JPA entities.
- Global exception handling returns consistent JSON error messages and avoids known 5xx cases.
