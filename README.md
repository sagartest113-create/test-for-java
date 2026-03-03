# test-for-java

Demo Spring Boot application for **TestCraft AI** — an AI tool that creates test files and test cases for pull request (PR) changes.

## Technology stack

| Area    | Technology              |
|---------|-------------------------|
| Runtime | Java 21                 |
| Framework | Spring Boot 3.2.4    |
| Build   | Maven                   |
| Web     | Spring Web MVC, Spring Security |

## Run the application

```bash
./mvnw spring-boot:run
```

Or with a local Maven install:

```bash
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

## Endpoints

- **GET /health** — No auth. Returns `{"status":"UP", ...}`.
- **GET /api/items** — List items (HTTP Basic: `user` / `user`).
- **GET /api/items/{id}** — Get item by ID.
- **POST /api/items** — Create item (JSON body).
- **PUT /api/items/{id}** — Update item.
- **DELETE /api/items/{id}** — Delete item.

Example with curl:

```bash
# Health (no auth)
curl http://localhost:8080/health

# API (Basic auth user:user)
curl -u user:user http://localhost:8080/api/items
```

## Project layout

- `src/main/java/com/testcraft/demo/` — Application, controllers, service, model, config.
- `src/main/resources/` — `application.properties`.
- `src/test/java/` — Placeholder for tests (for TestCraft AI to generate).

Use this repo as the target for TestCraft AI setup and PR-based test generation.
