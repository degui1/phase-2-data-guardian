# Data Guardian — FIAP Tech Challenge Phase 2

REST API for managing users, user types, restaurants, opening hours, and menu items. Registration and login issue JWT access tokens; restaurant and menu changes require ownership of the restaurant.

## Technology stack

| Technology | Version / purpose |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.1 |
| Maven Wrapper | 3.10.0 |
| Spring Web MVC and Validation | REST endpoints and request validation |
| Spring Data JPA / Hibernate | Relational persistence |
| Spring Security / OAuth2 Resource Server | JWT authentication and authorization |
| MySQL | 8.4 in Docker Compose |
| Flyway | Versioned schema migrations and initial user types |
| springdoc-openapi | 3.2.0; Swagger UI and OpenAPI |
| JUnit, Mockito, MockMvc, H2 | Unit, HTTP, security, and integration tests |
| JaCoCo | Coverage reporting and an 80% line coverage requirement |
| Docker Compose | MySQL-only development and full application environments |

Spring Boot manages dependency versions unless explicitly overridden in `pom.xml`.

## Features and architecture

- User registration, login, authenticated profile, and user CRUD.
- User type CRUD and user/type association.
- Restaurant CRUD with embedded addresses and opening periods.
- Menu item CRUD scoped to its restaurant, with prices stored as integer cents.
- Pagination, validation, and consistent ProblemDetail error responses.

The project separates domain entities and services, application use case/repository/security interfaces, and infrastructure implementations. Controllers call use cases; persistence adapters implement repository interfaces and map domain objects to JPA entities.

```text
src/main/java/com/api_application/phase_2_data_guardian/
  domain/         Entities, business services, and domain exceptions
  application/    Use cases, repository/security interfaces, and models
  infra/          REST controllers, DTOs, mappers, persistence, and security
src/main/resources/db/migration/   Flyway SQL migrations
src/test/java/                    Tests organized by layer and resource
postman/                          API collection and environment
```

Relationships: user type → users → restaurants → menu items. Opening periods belong to restaurants.

## Requirements and configuration

For the full Docker environment, install Docker with Compose. For local application development and tests, also install JDK 21 and set `JAVA_HOME`. Maven is provided by the wrapper. The first build requires access to dependency and container registries.

From the project root, copy the environment template:

```powershell
Copy-Item .env.example .env
```

On Linux/macOS, use `cp .env.example .env`. Set your database passwords and generate a JWT secret. Keep `.env` out of Git.

| Variable | Local setting / default | Purpose |
| --- | --- | --- |
| DB_HOST | localhost | Database host for Java running locally |
| DB_PORT | 3307 | Host port published by the MySQL container |
| DB_NAME | phase_2_data_guardian | Application database |
| DB_USERNAME | data_guardian | Application database user; must not be root |
| DB_PASSWORD | Set in .env | Application database password |
| MYSQL_ROOT_PASSWORD | Set in .env | MySQL administrator password |
| JPA_DDL_AUTO | validate | Validate the schema managed by Flyway |
| JPA_SHOW_SQL | false | Log SQL statements |
| JPA_FORMAT_SQL | false | Format SQL logs |
| JWT_SECRET | Set in .env | Base64-encoded secret of at least 32 random bytes |
| JWT_ISSUER | phase-2-data-guardian | Expected JWT issuer |
| JWT_EXPIRATION_SECONDS | 3600 | Token lifetime in seconds |
| API_DOCS_ENABLED | true | Enable Swagger UI and OpenAPI |
| SERVER_PORT | 8080 | HTTP port when running Java locally |

Generate the JWT secret using PowerShell 7:

```powershell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Alternatively, use `openssl rand -base64 32`. Save the result as `JWT_SECRET` in `.env`. Changing it invalidates existing tokens.

Java optionally imports `.env` through `spring.config.import` when started from the project root. Operating system environment variables take precedence. Compose reads `.env` for interpolation and passes the variables explicitly listed in each service's `environment` section.

## Run with Docker Compose

### Database only: local development

```bash
docker compose up -d
```

Without an active profile, only `data-guardian-db` starts. Run Java from your IDE with the project root as its working directory, or use:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux/macOS, use `./mvnw spring-boot:run`.

The local application connects to `localhost:3307` by default. The MySQL port is bound to `127.0.0.1`; change `DB_PORT` if that host port is already in use.

### Database and application: full environment

```bash
docker compose --profile full up -d --build
```

The `app` service belongs to the `full` profile. Enabling it starts both services, builds the Dockerfile, and waits for MySQL to become healthy before starting Java.

| Connection | Address |
| --- | --- |
| Local Java → MySQL | localhost:${DB_PORT}, default 3307 |
| Containerized application → MySQL | data-guardian-db:3306 |
| Client → API | http://localhost:8080 |

The containerized application receives its database credentials and JWT secret from Compose. `DB_HOST` and `DB_PORT` are explicitly set to the internal service address; local host port changes do not affect this connection. Other optional settings must be added to the service's `environment` section to override application defaults.

The Dockerfile compiles with Maven 3.10.0 and JDK 21, then runs the JAR in a separate JRE 21 image. Its build command skips test execution; run the verification command below separately.

```bash
# Inspect services and logs
docker compose --profile full ps
docker compose --profile full logs -f app data-guardian-db

# Stop and remove both services while retaining database data
docker compose --profile full down
```

To switch from the full environment to Java running locally, stop the `app` container first with `docker compose --profile full stop app`, then start Java locally. Starting only the database does not stop an application container that is already running.

MySQL data is stored in the named `app` volume. Removing containers retains the volume. MySQL initialization variables create the database and user only when the volume is empty; changing passwords in `.env` does not update existing database accounts.

## Database migrations

Flyway automatically applies migrations from `src/main/resources/db/migration` before JPA validation.

| Migration | Creates / initializes |
| --- | --- |
| V1 | user_types |
| V2 | users |
| V3 | restaurants and address columns |
| V4 | restaurant_opening_periods |
| V5 | menu_items |
| V6 | CUSTOMER and RESTAURANT_OWNER user types |

The schema includes foreign keys and uniqueness constraints. Flyway records applied scripts in `flyway_schema_history`; Hibernate uses `ddl-auto=validate` to check the resulting schema. User type IDs are generated, so clients must discover them rather than assume fixed values.

Start with an empty application database for the first migration run. A pre-existing schema created by Hibernate needs a deliberate migration/baseline strategy. Do not enable automatic baseline without checking schema compatibility. The manual `postman/setup-user-types.sql` script is unnecessary when V6 has run.

## Authentication and business rules

Register with `POST /auth/register` or sign in with `POST /auth/login`. Both return `accessToken`, `tokenType`, `expiresIn`, and the user's current details. Registration assigns the CUSTOMER type.

```json
{
  "name": "Maria Silva",
  "email": "maria@example.com",
  "password": "Guardian@123"
}
```

For login, send only `email` and `password`. Registration passwords must contain 8–64 characters, uppercase and lowercase letters, a number, and a special character, without spaces.

Protected requests use:

```http
Authorization: Bearer <accessToken>
```

- JWT subject identifies the user; no X-User-Id header is needed.
- Users may change only their own type to CUSTOMER or RESTAURANT_OWNER. There is no ADMIN role.
- Custom types can exist in the catalog but cannot be self-assigned.
- Only RESTAURANT_OWNER users may manage the type catalog or create restaurants.
- Restaurant and menu mutations require ownership. A restaurant owner cannot switch to CUSTOMER while owning restaurants.
- A restaurant with menu items cannot be deleted until those items are removed.
- User profile reads, updates, and deletion are restricted to the same user. User listing exposes only ID, name, and type; passwords and hashes are never returned.
- Types associated with users cannot be deleted or renamed to a different name.
- Opening periods must start before they end on the same day and cannot overlap. Adjacent periods are allowed; overnight periods must be split between days.

## Routes

All routes except registration, login, and API documentation require a JWT. Resource IDs are Long values. There is no `/api/v1` prefix.

| Method | Route | Success | Behavior |
| --- | --- | --- | --- |
| POST | /auth/register | 201 | Register and issue JWT |
| POST | /auth/login | 200 | Authenticate and issue JWT |
| GET | /auth/me | 200 | Current authenticated profile |
| GET | /users | 200 | Paginated user summaries |
| GET | /users/{userId} | 200 | Own profile |
| PUT | /users/{userId} | 200 | Update own name and email |
| DELETE | /users/{userId} | 204 | Delete own account without restaurants |
| GET | /users/{userId}/user-type | 200 | Read associated type |
| PUT | /users/{userId}/user-type | 200 | Change own type |
| GET | /user-types | 200 | Paginated types |
| GET | /user-types/{userTypeId} | 200 | Read type |
| POST | /user-types | 201 | Create type |
| PUT | /user-types/{userTypeId} | 200 | Update type |
| DELETE | /user-types/{userTypeId} | 204 | Delete unused type |
| GET | /restaurants | 200 | Paginated restaurants |
| GET | /restaurants/{restaurantId} | 200 | Read restaurant |
| POST | /restaurants | 201 | Create restaurant |
| PUT | /restaurants/{restaurantId} | 200 | Update owned restaurant |
| DELETE | /restaurants/{restaurantId} | 204 | Delete owned restaurant without items |
| GET | /restaurants/{restaurantId}/menu-items | 200 | Paginated restaurant menu |
| GET | /restaurants/{restaurantId}/menu-items/{menuItemId} | 200 | Read item in this restaurant |
| POST | /restaurants/{restaurantId}/menu-items | 201 | Create item in owned restaurant |
| PUT | /restaurants/{restaurantId}/menu-items/{menuItemId} | 200 | Update item in owned restaurant |
| DELETE | /restaurants/{restaurantId}/menu-items/{menuItemId} | 204 | Delete item in owned restaurant |

Creation responses include `Location`. Successful deletion responses have no body. Menu lookup uses both restaurant and item IDs; using another restaurant's URL returns 404.

### Request examples

Change your user type using an ID returned by `GET /user-types`:

```json
{ "userTypeId": 2 }
```

Create or update a restaurant. The owner comes from the JWT, not the request body. Include all seven days; an empty list means closed.

```json
{
  "name": "Guardian Restaurant",
  "address": {
    "street": "Rua das Flores",
    "number": "100",
    "complement": "Store 1",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01001-000"
  },
  "cuisineType": "Brazilian",
  "openingHours": {
    "monday": [{ "opensAt": "08:00:00", "closesAt": "18:00:00" }],
    "tuesday": [],
    "wednesday": [],
    "thursday": [],
    "friday": [],
    "saturday": [],
    "sunday": []
  }
}
```

Create or update a menu item. `2590` represents BRL 25.90. `imagePath` is an image reference; this API does not provide an upload endpoint.

```json
{
  "name": "Daily special",
  "description": "Rice, beans and vegetables",
  "priceInCents": 2590,
  "dineInOnly": false,
  "imagePath": "/images/daily-special.jpg"
}
```

### Pagination and errors

```http
GET /restaurants?page=0&size=10&sort=name,asc
GET /restaurants/1/menu-items?page=0&size=10&sort=priceInCents,desc
```

Pages are zero-based; default page size is 10 and default ordering is `name,asc`. User directory ordering accepts `id` and `name`. Responses contain `content` and pagination metadata.

Errors use `application/problem+json` and Spring ProblemDetail:

| Status | Meaning |
| --- | --- |
| 400 | Invalid request, price, or opening hours |
| 401 | Missing/invalid/expired token or invalid login credentials |
| 403 | Operation not authorized |
| 404 | Resource not found |
| 409 | Duplicate email/name, type in use, or relationship/integrity conflict |

## Swagger and OpenAPI

With the application running:

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)
- [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

Register or log in, copy `accessToken`, and enter only the token in Swagger's **Authorize** dialog. Discover RESTAURANT_OWNER through `GET /user-types`, update your own type, and create restaurants/menu items. Set `API_DOCS_ENABLED=false` to disable documentation when configuring the application environment.

## Postman collection

Import both files from `postman/`:

- [Collection: 53 requests](postman/data-guardian.postman_collection.json)
- [Local environment](postman/local.postman_environment.json)

Select **Data Guardian — Local**, set `baseUrl` to `http://localhost:8080`, and run folders **00 through 07** in order using the Collection Runner, with one iteration. Keep API documentation enabled for folder 00.

The flow registers two users with unique emails, discovers type IDs, captures JWTs, changes the owner's type, logs in, exercises CRUD/pagination/negative cases, and removes the created resources and accounts. Tokens and IDs are captured automatically. Run from the beginning rather than starting with requests that depend on unset IDs. An interrupted run can leave test resources in the database.

Flyway V6 supplies the required initial types; no manual SQL setup is needed. Clear tokens and credentials before sharing an exported environment. The collection exercises mutable development data.

## Build and tests

| Task | Windows PowerShell | Linux/macOS |
| --- | --- | --- |
| Run locally | .\mvnw.cmd spring-boot:run | ./mvnw spring-boot:run |
| Compile | .\mvnw.cmd compile | ./mvnw compile |
| Run tests | .\mvnw.cmd test | ./mvnw test |
| Verify coverage and package | .\mvnw.cmd clean verify | ./mvnw clean verify |
| Package without running tests | .\mvnw.cmd package -DskipTests | ./mvnw package -DskipTests |

Run the packaged application from the project root with the same configuration:

```bash
java -jar target/phase-2-data-guardian-0.0.1-SNAPSHOT.jar
```

Tests are organized into `services`, `controllers`, `mappers`, `integration`, `security`, and shared `support` packages. Integration tests activate the `test` profile and use isolated H2 databases in MySQL mode, including real BCrypt/JWT flows.

Recorded verification with Java 21: **174 tests passed**, with no failures, errors, or skipped tests. Coverage: **92.45% lines**, **90.00% branches**, **94.21% instructions**. These results describe the H2 test environment.

`verify` enforces at least **80% line coverage**, without production class exclusions. `test` generates reports but does not enforce the threshold. Maven configures Mockito and JaCoCo agents automatically.

- HTML coverage report: `target/site/jacoco/index.html`
- XML/CSV coverage: `target/site/jacoco/jacoco.xml` and `jacoco.csv`
- Individual test reports: `target/surefire-reports/`

### Run integration tests with MySQL

H2 remains the default for `./mvnw clean verify`. To use the isolated MySQL test database instead:

```powershell
docker compose -f docker-compose.test.yml up -d --wait
.\mvnw.cmd "-Dtest.database=mysql" clean verify
docker compose -f docker-compose.test.yml down
```

On Linux/macOS, replace `.\mvnw.cmd` with `./mvnw`. In the IDE, add `-Dtest.database=mysql` to the test JVM options. The test profile resolver accepts only `h2` (default) or `mysql`.

The test database uses localhost:3308, its own Compose project, and temporary storage. Its credentials are disposable local test values. Hibernate uses create-drop and Flyway is disabled in both test profiles because the tests create their own fixtures. These runs test persistence behavior; migration validation uses the full application environment with Flyway enabled. Never point this profile at a database whose data must be retained.
