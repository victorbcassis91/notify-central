# Notify Central

Fullstack notification project with **Spring Boot backend** and **React + MobX frontend**, integrating Kafka, M2 database, Redis, and WebSocket for real-time bidirectional communication.

---

## Project Structure

/backend - Spring Boot backend
/frontend - React + MobX frontend

---

## Backend (Spring Boot)

### Technologies
- Spring Boot
- "Kafka (Producer & Consumer)" or "Asynchronous executor for notifications"
- M2 Database (e.g., H2, MySQL, or PostgreSQL)
- Redis (cache or pub/sub)
- WebSocket structure for real-time notifications (**prototype, not production-ready**)

### Setup

1. Configure M2 database in `application.yml`.


2. Configure Redis and Kafka:
```
notifycentral.notification-type: "executor" or "kafka"

spring.kafka.bootstrap-servers=localhost:9092

spring.redis.host=localhost
spring.redis.port=6379
```

3. Build and run:
```
cd backend
mvn clean install
mvn spring-boot:run
```

### Features
- Produces and consumes messages via Kafka
- Persists notification logs in M2 database
- Cache and pub/sub using Redis
- Asynchronous notification sending with executor
- Real-time communication support via WebSocket

## Frontend (React + MobX)

### Technologies
- React
- MobX (state management)
- Material UI (optional for UI)
- WebSocket for bidirectional communication with backend  (**prototype, not production-ready**)

### Setup
1. Install dependencies:
```
cd frontend
npm install
```

2. Configure .env with backend and WebSocket URLs:
```
REACT_APP_API_URL=http://localhost:8080
REACT_APP_WS_URL=ws://localhost:8080/ws
```

3. Run the application:
```
npm run dev
```

### Features
- Notification dashboard
- Send notifications via UI
- Receive real-time notifications via WebSocket
- State managed with MobX

---

## Running the environment (Kafka and Redis)

1. Make sure you have Docker installed.
2. From the project root, run:
```
docker-compose up -d
```
3. Verify the services are running:
```
- Kafka: localhost:9092
- Zookeeper: localhost:2181
- Redis: localhost:6379
- Kafka UI: http://localhost:5000
```
4. Stop the environment:
```
docker-compose down
```

#### Notes

- The Redis service is used for caching and pub/sub in development.
- Kafka UI allows you to monitor topics and messages.
- This setup is for development/testing purposes only. Do not use in production.

---

## Running Tests

### Backend
```
cd backend
mvn test
```

### Frontend
```
cd frontend
npm test
```

---

## Bidirectional Communication (WebSocket) (**prototype, not production-ready**)
Backend exposes a WebSocket endpoint for real-time communication to all clients:
