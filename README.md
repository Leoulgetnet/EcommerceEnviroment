# Ecommerce Microservices Environment

A full-stack ecommerce microservices project built with Spring Boot, Spring Cloud Gateway, gRPC, Kafka, PostgreSQL, Keycloak, and a React/Vite frontend.

The project is organized as independent services that communicate through REST, gRPC, and Kafka. It is designed as a practical learning and development environment for building secured, distributed ecommerce workflows.

## Features

- API Gateway for routing requests to backend services
- Product service with product CRUD APIs and gRPC stock operations
- Order service with order APIs, gRPC integration, Kafka consumer, and Server-Sent Events support
- Payment service with REST APIs, gRPC clients, and Kafka producer support
- Keycloak-based OAuth2/JWT security
- PostgreSQL persistence with Spring Data JPA
- Kafka and Kafka UI for event-driven communication
- gRPC with Protocol Buffers for service-to-service calls
- Actuator endpoints for service health and metrics
- Resilience4j retry and circuit breaker configuration
- React frontend built with Vite

## Architecture

```text
React Frontend
      |
      v
API Gateway :8084
      |
      +--> Product Service :8081  | gRPC :7000
      |
      +--> Order Service   :8082  | gRPC :7001
      |
      +--> Payment Service :8083

Shared infrastructure:
- PostgreSQL :5432
- Keycloak   :9090
- Kafka      :9094
- Kafka UI   :6080
- pgAdmin    :5050
```

## Project Structure

```text
.
├── ApiGateWay/                 # Spring Cloud Gateway and auth endpoints
├── ProductService/             # Product REST APIs, product gRPC server, Docker Compose infra
├── OrderService/               # Order REST APIs, order gRPC server, Kafka consumer, SSE
├── PaymentService/             # Payment REST APIs, gRPC clients, Kafka producer
└── FrontEndMs/
    └── EcommerceMsFrontEnd/    # React + Vite frontend
```

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Spring Security OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL
- gRPC
- Protocol Buffers
- Kafka
- Resilience4j
- Micrometer / OpenTelemetry tracing support

### Frontend

- React
- Vite
- ESLint

### Infrastructure

- Docker Compose
- PostgreSQL
- pgAdmin
- Keycloak
- Kafka in KRaft mode
- Kafka UI

## Service Ports

| Service | Port |
| --- | ---: |
| Product Service | 8081 |
| Order Service | 8082 |
| Payment Service | 8083 |
| API Gateway | 8084 |
| Product gRPC | 7000 |
| Order gRPC | 7001 |
| PostgreSQL | 5432 |
| Keycloak | 9090 |
| Kafka external listener | 9094 |
| Kafka UI | 6080 |
| pgAdmin | 5050 |

## API Routes

Requests can be sent directly to services during development or through the API Gateway.

### Gateway Routes

| Route | Target |
| --- | --- |
| `/api/product/**` | Product Service |
| `/api/order/**` | Order Service |
| `/api/paymentservice/**` | Payment Service |
| `/auth/**` | Gateway authentication endpoints |

### Main Endpoints

Product Service:

- `GET /api/product`
- `GET /api/product/{id}`
- `POST /api/product`
- `PUT /api/product/{id}`
- `GET /api/product/circuitbreaker`
- `GET /api/test/public`
- `GET /api/test/secure`
- `GET /api/test/user-info`

Order Service:

- `GET /api/order`
- `GET /api/order/{id}`
- `POST /api/order`
- `PUT /api/order/{id}/status`
- `GET /api/order/sse/subscribe`

Payment Service:

- `GET /api/paymentservice/{id}`
- `GET /api/paymentservice/check`

Gateway Auth:

- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

## Prerequisites

Install the following before running the project:

- Java 21
- Docker Desktop
- Node.js and npm
- Git

Each backend service includes a Maven wrapper, so Maven does not need to be installed globally.

## Local Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd "Full Microservice Enviroment"
```

### 2. Start infrastructure services

The Docker Compose file is currently inside `ProductService`.

```bash
cd ProductService
docker compose up -d
```

This starts PostgreSQL, pgAdmin, Keycloak, Kafka, and Kafka UI.

Useful local URLs:

- Keycloak: `http://localhost:9090`
- pgAdmin: `http://localhost:5050`
- Kafka UI: `http://localhost:6080`

Default development credentials from the compose file:

- PostgreSQL: `root` / `root`
- pgAdmin: `admin@admin.com` / `admin`
- Keycloak admin: `admin` / `admin`
- Kafka UI: `admin` / `admin123`

### 3. Configure Keycloak

The services expect a Keycloak realm named:

```text
EcommerceMs
```

Default issuer URL:

```text
http://localhost:9090/realms/EcommerceMs
```

For local development, create the realm and client used by the services, or provide a realm import under:

```text
ProductService/keycloak-config/
```

For production or public deployments, replace development credentials and client secrets with secure values.

### 4. Run backend services

Open separate terminals for each service.

Product Service:

```bash
cd ProductService
./mvnw spring-boot:run
```

Order Service:

```bash
cd OrderService
./mvnw spring-boot:run
```

Payment Service:

```bash
cd PaymentService
./mvnw spring-boot:run
```

API Gateway:

```bash
cd ApiGateWay
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
.\mvnw.cmd spring-boot:run
```

### 5. Run the frontend

```bash
cd FrontEndMs/EcommerceMsFrontEnd
npm install
npm run dev
```

Vite will print the local frontend URL, usually:

```text
http://localhost:5173
```

## Environment Variables

The backend services support these environment variables:

| Variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/ecommerce` | PostgreSQL JDBC URL |
| `DB_USER` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `KEYCLOAK_URL` | `http://localhost:9090/realms/EcommerceMs` | Keycloak issuer URL |

Example:

```bash
DB_URL=jdbc:postgresql://localhost:5432/ecommerce DB_USER=root DB_PASSWORD=root ./mvnw spring-boot:run
```

## gRPC Services

Product Service exposes:

- `ProductInformation/getStockInformation`
- `ProductInformation/deductStockRequest`

Order Service exposes:

- `orderInformation/getOrderInformation`
- `orderInformation/changeOrderStatus`

The `.proto` files are located in each service under:

```text
src/main/proto/
```

## Kafka

Kafka runs through Docker Compose and is exposed locally on:

```text
localhost:9094
```

Current messaging flow:

- Payment Service is configured as a Kafka producer.
- Order Service listens to the `ordercompletetion` topic using group ID `ordercompletiongroupid`.
- Kafka UI is available at `http://localhost:6080`.

## Testing

Run tests for a backend service:

```bash
cd ProductService
./mvnw test
```

Run frontend linting:

```bash
cd FrontEndMs/EcommerceMsFrontEnd
npm run lint
```

## Build

Build a backend service:

```bash
cd ProductService
./mvnw clean package
```

Build the frontend:

```bash
cd FrontEndMs/EcommerceMsFrontEnd
npm run build
```

## Notes

- This repository is configured for local development.
- The Docker Compose setup lives in `ProductService/docker-compose.yml`.
- Some infrastructure values are development defaults and should be changed before deployment.
- Keycloak realm/client setup is required before secured endpoints can be used successfully.
- Backend services use `spring.jpa.hibernate.ddl-auto=update`, which is convenient locally but should be reviewed before production use.

## License

Add your preferred license before publishing this repository publicly.
