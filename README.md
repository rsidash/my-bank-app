# My Bank — Микросервисное приложение

Банковское приложение, построенное на микросервисной архитектуре с использованием Spring Boot, OAuth 2.0 и Apache Kafka.
Поддерживает развёртывание в Docker Compose (dev) и Kubernetes (prod) через Helm.

## Архитектура

### Kubernetes (prod)
В Kubernetes Spring Cloud Gateway заменён на **Ingress**, а Eureka и Config Server — на нативный **Kubernetes Service Discovery** и **ConfigMaps/Secrets**.
Взаимодействие с Notifications Service осуществляется через **Apache Kafka**.

```
┌─────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                     │
│                                                          │
│  ┌─────────┐    ┌──────────────┐   ┌─────────────────┐ │
│  │ Ingress │───>│ front-app    │   │ keycloak        │ │
│  │ (nginx) │    └──────────────┘   │ (NodePort:30180)│ │
│  │         │───>│accounts-svc  │   └─────────────────┘ │
│  │         │───>│cash-svc      │                        │
│  │         │───>│transfer-svc  │   ┌─────────────────┐ │
│  └─────────┘    └──────────────┘   │ accounts-db     │ │
│                                     │ (PostgreSQL)    │ │
│  ┌──────────┐   ┌──────────────┐   └─────────────────┘ │
│  │  Kafka   │<──│accounts-svc  │                        │
│  │ (KRaft)  │<──│cash-svc      │                        │
│  │          │<──│transfer-svc  │                        │
│  │          │──>│notif-svc     │                        │
│  └──────────┘   └──────────────┘                        │
└─────────────────────────────────────────────────────────┘
```

### Docker Compose (dev)
Сохранена совместимость: Eureka + Config Server + Spring Cloud Gateway работают как раньше.
Kafka добавлен как отдельный контейнер.

## Модули

| Модуль | Описание | Порт |
|--------|----------|------|
| `accounts-service` | Микросервис аккаунтов | 8081 |
| `cash-service` | Микросервис обналичивания | 8082 |
| `notifications-service` | Сервис уведомлений (Kafka consumer) | — |
| `transfer-service` | Сервис переводов | 8084 |
| `front-app` | Фронт-приложение (Thymeleaf) | 8080 |
| `eureka-server` | Service Discovery (только Docker) | 8761 |
| `config-server` | Config Server (только Docker) | 8888 |
| `gateway` | API Gateway (только Docker) | 8090 |

## Технологии

- Java 21
- Spring Boot 3.5.7
- Spring Security OAuth 2.0 (Authorization Code + Client Credentials)
- Spring Data JPA + PostgreSQL
- **Apache Kafka** (межсервисное взаимодействие с Notifications)
- Thymeleaf
- Keycloak (сервер авторизации)
- Docker / Docker Compose
- **Kubernetes + Helm**
- **NGINX Ingress Controller**
- **Micrometer Tracing + Zipkin** (распределённый трейсинг)
- **Micrometer + Prometheus** (метрики)
- **ELK Stack** (Elasticsearch + Logstash + Kibana — логирование)
- **Grafana** (визуализация метрик и алерты)
- Lombok
- Maven (multi-module)

## Предварительные требования

### Для Docker Compose
- Java 21+
- Maven 3.9+
- Docker и Docker Compose

### Для Kubernetes
- Java 21+
- Maven 3.9+
- Docker (для сборки образов)
- kubectl
- Helm 3.x
- Kubernetes кластер (minikube / kind / etc.)
- NGINX Ingress Controller

---

## Сборка

```bash
./mvnw clean package -DskipTests
```

Windows:
```cmd
mvnw.cmd clean package -DskipTests
```

### Сборка Docker-образов

```bash
docker build -t my-bank/accounts-service ./accounts-service
docker build -t my-bank/cash-service ./cash-service
docker build -t my-bank/transfer-service ./transfer-service
docker build -t my-bank/notifications-service ./notifications-service
docker build -t my-bank/front-app ./front-app
```

---

## Запуск в Kubernetes (Helm)

### 1. Подготовка кластера

```bash
# Minikube
minikube start
minikube addons enable ingress

# Или kind с ingress
```

### 2. Сборка образов

```bash
./mvnw clean package -DskipTests

# Для minikube — загрузить образы в minikube
eval $(minikube docker-env)
docker build -t my-bank/accounts-service ./accounts-service
docker build -t my-bank/cash-service ./cash-service
docker build -t my-bank/transfer-service ./transfer-service
docker build -t my-bank/notifications-service ./notifications-service
docker build -t my-bank/front-app ./front-app
```

### 3. Установка Helm-чарта

```bash
cd helm/my-bank
helm dependency update
helm install my-bank . --namespace my-bank --create-namespace
```

### 4. Проверка статуса

```bash
kubectl get pods -n my-bank
kubectl get svc -n my-bank
kubectl get ingress -n my-bank
```

### 5. Запуск тестов чарта

```bash
helm test my-bank -n my-bank
```

### 6. Доступ к приложению

Добавить в `/etc/hosts` (или `C:\Windows\System32\drivers\etc\hosts`):
```
<MINIKUBE_IP>  my-bank.local
```

- Приложение: http://my-bank.local (через Ingress)
- Приложение (NodePort): http://localhost:30080
- Keycloak Admin: http://localhost:30180 (admin/admin)

### 7. Обновление

```bash
helm upgrade my-bank . --namespace my-bank
```

### 8. Удаление

```bash
helm uninstall my-bank --namespace my-bank
kubectl delete namespace my-bank
```

### Переопределение значений

```bash
helm install my-bank . --namespace my-bank --create-namespace \
  --set global.secrets.frontAppClientSecret=my-secret \
  --set global.secrets.accountsClientSecret=acc-secret \
  --set global.ingress.host=myapp.example.com
```

---

## Запуск в Docker Compose (dev)

### 1. Поднять инфраструктуру

```bash
docker compose up keycloak accounts-db kafka -d
```

### 1.1. Поднять мониторинг (опционально)

```bash
cd monitoring
docker compose up -d
```

- Zipkin: http://localhost:9411
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- Kibana: http://localhost:5601

### 2. Запустить сервисы из IDE

В следующем порядке:

1. `EurekaServerApplication`
2. `ConfigServerApplication` — с переменными окружения:
   ```
   KEYCLOAK_CLIENT_SECRET=my-bank-front-secret
   ACCOUNTS_CLIENT_SECRET=accounts-service-secret
   ACCOUNTS_DB_PASSWORD=accounts_password
   CASH_CLIENT_SECRET=cash-service-secret
   TRANSFER_CLIENT_SECRET=transfer-service-secret
   ```
3. `GatewayApplication`
4. `AccountsServiceApplication`
5. `CashServiceApplication`
6. `TransferServiceApplication`
7. `NotificationsServiceApplication`
8. `MyBankFrontAppApplication`

### 3. Проверить

- Eureka: http://localhost:8761
- Keycloak: http://localhost:8180 (admin/admin)
- Приложение: http://localhost:8080

---

## Запуск в Docker Compose (полный)

```bash
./mvnw clean package -DskipTests
docker compose up --build -d
docker compose ps
```

- Приложение: http://localhost:8080
- Eureka: http://localhost:8761
- Keycloak: http://localhost:8180

Остановить:
```bash
docker compose down -v
```

---

## Тестирование

### Unit и интеграционные тесты

```bash
./mvnw test
```

Тесты автоматически отключают Eureka и Config Server через test-профиль.
Kafka-тесты используют `@EmbeddedKafka` для интеграционного тестирования.

### Helm chart тесты

```bash
# После установки чарта
helm test my-bank -n my-bank
```

### Валидация шаблонов Helm (без установки)

```bash
cd helm/my-bank
helm template my-bank . | kubectl apply --dry-run=client -f -
```

---

## Тестовые пользователи

| Логин | Пароль | Баланс |
|-------|--------|--------|
| ivanov | password | 100 руб |
| petrov | password | 200 руб |
| sidorov | password | 300 руб |

---

## Структура Helm-чарта

```
helm/my-bank/
├── Chart.yaml                 # Зонтичный чарт
├── values.yaml                # Глобальные настройки
├── templates/
│   ├── _helpers.tpl
│   ├── ingress.yaml           # Заменяет Spring Cloud Gateway
│   └── tests/
│       ├── test-connection.yaml
│       └── test-db.yaml
└── charts/
    ├── accounts-db/           # PostgreSQL (StatefulSet)
    ├── kafka/                 # Apache Kafka KRaft (StatefulSet)
    ├── keycloak/              # Keycloak (Deployment + NodePort)
    ├── accounts-service/      # Deployment + Service + ConfigMap + Secret
    ├── cash-service/
    ├── transfer-service/
    ├── notifications-service/ # Deployment + ConfigMap (без Service — только Kafka)
    └── front-app/             # Deployment + NodePort Service
```

---

## Kafka Topics

| Topic | Producer | Consumer |
|-------|----------|----------|
| `notifications.account-updated` | accounts-service | notifications-service |
| `notifications.transfer` | transfer-service | notifications-service |
| `notifications.cash-deposit` | cash-service | notifications-service |
| `notifications.cash-withdraw` | cash-service | notifications-service |

---

## Kubernetes vs Docker: что заменено

| Docker Compose | Kubernetes |
|---------------|-----------|
| Eureka Server | Kubernetes Service Discovery (DNS) |
| Spring Cloud Config | ConfigMaps + Secrets |
| Spring Cloud Gateway | NGINX Ingress |
| docker-compose.yml | Helm Chart |
| Environment vars | ConfigMap + Secret envFrom |

---

## OAuth 2.0 Flow

| Компонент | Flow | Назначение |
|-----------|------|-----------|
| Front App | Authorization Code | Пользовательская авторизация |
| Cash Service | Client Credentials | Запросы в Accounts |
| Transfer Service | Client Credentials | Запросы в Accounts |
| Ingress | — | Маршрутизация (без JWT-валидации) |
| Notifications | Kafka Consumer | Получение событий через Kafka |

---

## Доступ к Keycloak из кластера

Сервисы внутри кластера обращаются к Keycloak по внутреннему DNS:
```
http://<release-name>-keycloak:8180/realms/my-bank
```

Браузер пользователя обращается через NodePort:
```
http://localhost:30180/realms/my-bank
```

Если Keycloak снаружи кластера, создайте ExternalName Service:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: keycloak-external
spec:
  type: ExternalName
  externalName: keycloak.example.com
```
И обновите `global.keycloak.issuerUri` в `values.yaml`.

---

## Мониторинг

### Компоненты

| Компонент | Назначение | URL |
|-----------|-----------|-----|
| Zipkin | Распределённый трейсинг | http://localhost:9411 |
| Prometheus | Сбор метрик | http://localhost:9090 |
| Grafana | Дашборды и алерты | http://localhost:3000 |
| Elasticsearch | Хранение логов | http://localhost:9200 |
| Logstash | Сбор и обработка логов | localhost:5044 |
| Kibana | Просмотр логов | http://localhost:5601 |

### Трейсинг (Zipkin)

Каждый сервис:
- Генерирует/пропагирует trace ID и span ID через HTTP-заголовки (B3 propagation)
- Создаёт дочерние спаны для запросов в БД и Apache Kafka
- Отправляет трейсы в Zipkin через Micrometer Tracing + Brave

### Метрики (Prometheus + Grafana)

Каждый сервис экспортирует через `/actuator/prometheus`:
- HTTP-метрики (RPS, 4xx, 5xx, гистограммы latency)
- JVM-метрики (heap, threads, GC)
- Стандартные метрики Spring Boot

Алерты настроены на:
- High 5xx error rate (>5%)
- High latency (p95 > 2s)
- Service down
- High JVM heap usage (>90%)

### Логирование (ELK)

Каждый сервис:
- Логирует через SLF4J + Logback
- Включает traceId и spanId в каждую запись лога
- Отправляет логи в Logstash через TCP (logstash-logback-encoder)
- Логи доступны для просмотра в Kibana

### Запуск мониторинга

```bash
cd monitoring
docker compose up -d
```
