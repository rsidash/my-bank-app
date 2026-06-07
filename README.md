# My Bank — Микросервисное приложение

Банковское приложение, построенное на микросервисной архитектуре с использованием Spring Boot, Spring Cloud и OAuth 2.0.

## Модули

| Модуль | Описание | Порт |
|--------|----------|------|
| `eureka-server` | Service Discovery (Eureka) | 8761 |
| `config-server` | Externalized Config (Spring Cloud Config) | 8888 |
| `gateway` | API Gateway (Spring Cloud Gateway) | 8090 |
| `accounts-service` | Микросервис аккаунтов | 8081 |
| `cash-service` | Микросервис обналичивания | 8082 |
| `notifications-service` | Сервис уведомлений | 8083 |
| `transfer-service` | Сервис переводов | 8084 |
| `front-app` | Фронт-приложение (Thymeleaf) | 8080 |

## Технологии

- Java 21
- Spring Boot 3.5.7
- Spring Cloud 2025.0.0
- Spring Security OAuth 2.0 (Authorization Code Flow + Client Credentials Flow)
- Spring Cloud Gateway
- Spring Cloud Config (Native)
- Netflix Eureka
- Spring Data JPA + PostgreSQL
- Thymeleaf
- Keycloak (сервер авторизации)
- Docker / Docker Compose
- Lombok
- Maven (multi-module)

## Предварительные требования

- Java 21+
- Maven 3.9+ (или используйте встроенный `mvnw`)
- Docker и Docker Compose (для запуска инфраструктуры)

---

## Сборка

Сборка всех модулей одной командой из корня проекта:

```bash
./mvnw clean package -DskipTests
```

На Windows:

```cmd
mvnw.cmd clean package -DskipTests
```

В результате в каждом модуле появится `target/<module>-0.0.1-SNAPSHOT.jar` — executable JAR.

---

## Запуск в среде разработки (IDE)

### 1. Поднять инфраструктуру

Запустите PostgreSQL и Keycloak через Docker:

```bash
docker compose up keycloak accounts-db -d
```

### 2. Запустить сервисы из IDE

Запускайте Spring Boot приложения в следующем порядке (Run Configuration в IDE):

1. `EurekaServerApplication` (eureka-server)
2. `ConfigServerApplication` (config-server) — задать переменные окружения:
   ```
   KEYCLOAK_CLIENT_SECRET=my-bank-front-secret
   ACCOUNTS_CLIENT_SECRET=accounts-service-secret
   ACCOUNTS_DB_PASSWORD=accounts_password
   CASH_CLIENT_SECRET=cash-service-secret
   TRANSFER_CLIENT_SECRET=transfer-service-secret
   ```
3. `GatewayApplication` (gateway)
4. `AccountsServiceApplication` (accounts-service)
5. `CashServiceApplication` (cash-service)
6. `TransferServiceApplication` (transfer-service)
7. `NotificationsServiceApplication` (notifications-service)
8. `MyBankFrontAppApplication` (front-app)

### 3. Проверить

- Eureka Dashboard: http://localhost:8761
- Keycloak Admin: http://localhost:8180 (admin/admin)
- Приложение: http://localhost:8080

Логин: `ivanov`, пароль: `password`

---

## Запуск локально (из JAR)

### 1. Собрать проект

```bash
./mvnw clean package -DskipTests
```

### 2. Поднять инфраструктуру

```bash
docker compose up keycloak accounts-db -d
```

### 3. Запустить сервисы

Каждый сервис в отдельном терминале, в порядке:

```bash
# 1. Eureka Server
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar

# 2. Config Server (с переменными окружения)
KEYCLOAK_CLIENT_SECRET=my-bank-front-secret \
ACCOUNTS_CLIENT_SECRET=accounts-service-secret \
ACCOUNTS_DB_PASSWORD=accounts_password \
CASH_CLIENT_SECRET=cash-service-secret \
TRANSFER_CLIENT_SECRET=transfer-service-secret \
java -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar

# 3. Gateway
java -jar gateway/target/gateway-0.0.1-SNAPSHOT.jar

# 4. Accounts Service
java -jar accounts-service/target/accounts-service-0.0.1-SNAPSHOT.jar

# 5. Cash Service
java -jar cash-service/target/cash-service-0.0.1-SNAPSHOT.jar

# 6. Transfer Service
java -jar transfer-service/target/transfer-service-0.0.1-SNAPSHOT.jar

# 7. Notifications Service
java -jar notifications-service/target/notifications-service-0.0.1-SNAPSHOT.jar

# 8. Front App
java -jar front-app/target/my-bank-front-app-0.0.1-SNAPSHOT.jar
```

На Windows (cmd) вместо `\` используйте `set` для переменных окружения:

```cmd
set KEYCLOAK_CLIENT_SECRET=my-bank-front-secret
set ACCOUNTS_CLIENT_SECRET=accounts-service-secret
set ACCOUNTS_DB_PASSWORD=accounts_password
set CASH_CLIENT_SECRET=cash-service-secret
set TRANSFER_CLIENT_SECRET=transfer-service-secret
java -jar config-server\target\config-server-0.0.1-SNAPSHOT.jar
```

---

## Запуск в Docker (весь проект)

### 1. Собрать JAR

```bash
./mvnw clean package -DskipTests
```

### 2. Запустить все контейнеры

```bash
docker compose up --build -d
```

### 3. Проверить статус

```bash
docker compose ps
```

### 4. Проверить приложение

- Приложение: http://localhost:8080
- Eureka Dashboard: http://localhost:8761
- Keycloak Admin: http://localhost:8180 (admin/admin)

### 5. Остановить

```bash
docker compose down
```

Для полной очистки (с удалением данных БД):

```bash
docker compose down -v
```

---

## Тестовые пользователи

| Логин | Пароль | Баланс |
|-------|--------|--------|
| ivanov | password | 100 руб |
| petrov | password | 200 руб |
| sidorov | password | 300 руб |

---

## Структура config-repo

| Файл | Назначение |
|------|-----------|
| `application.properties` | Общие настройки (Eureka, Keycloak) |
| `my-bank-front-app.properties` | Front App (OAuth2 Client, порт) |
| `gateway.properties` | Gateway (маршруты, JWT) |
| `accounts-service.properties` | Accounts (БД, OAuth2) |
| `cash-service.properties` | Cash (OAuth2 Client Credentials) |
| `transfer-service.properties` | Transfer (OAuth2 Client Credentials) |
| `notifications-service.properties` | Notifications (JWT, порт) |

---

## OAuth 2.0 Flow

| Компонент | Flow | Назначение |
|-----------|------|-----------|
| Front App | Authorization Code | Пользовательская авторизация |
| Accounts Service | Client Credentials | Запросы в Notifications |
| Cash Service | Client Credentials | Запросы в Accounts и Notifications |
| Transfer Service | Client Credentials | Запросы в Accounts и Notifications |
| Gateway | Resource Server | Валидация JWT |
| Notifications | Resource Server | Валидация JWT (scope: notifications) |
