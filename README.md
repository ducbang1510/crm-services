# CRM Project 🚀

A modern **Customer Relationship Management (CRM)** backend built with **Java 17** and **Spring Boot 3.5.5**.

It features secure **OAuth2 authorization**, a **MySQL** database, and interactive **Swagger API docs** for smooth development and integration.

---

## ✨ Features

* Customer management (CRUD APIs)
* OAuth2 Authorization Server (secure login & tokens)
* Role-based access control
* RESTful API endpoints
* Swagger-powered documentation

---

## 🛠 Tech Stack

* **Java**: 17
* **Spring Boot**: 3.5.5
* **MySQL**: 8.x
* **OAuth2**: spring-boot-starter-oauth2-authorization-server
* **API Docs**: springdoc-openapi-starter-webmvc-ui 2.8.5 (OpenAPI 3)
* **Build Tool**: Maven

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Maven 3.9+
* MySQL 8.x

### Database Setup

```sql
CREATE DATABASE crm_db;
```

Update your `application.properties` with DB credentials:

```properties
spring.datasource.username=db-user
spring.datasource.password=db-password
```

### Run Application

```bash
mvn spring-boot:run
```

App runs at **[http://localhost:8080](http://localhost:8080)**

---

## 🔐 Authorization

OAuth2 Authorization Server endpoints:

* `/oauth2/authorize` – Authorization endpoint
* `/oauth2/token` – Token endpoint
* `/oauth2/jwks` – JWKS endpoint
* `/oauth2/introspect` – Token introspection

---

## 📑 API Documentation

* Swagger UI → **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
* OpenAPI JSON → **[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)**

---

## 🧩 Example Endpoints

* `POST /api/v1/user` – Create a new user
* `GET /api/v1/user` – Get user info
* ...

(All require valid OAuth2 access tokens)

---

## 🔑 Sample OAuth2 Client Config (Authorization Code Grant)

**Client Info (for testing):**

* **Client ID**: `crm-app`
* **Client Secret**: `secret`
* **Redirect URI**: `http://localhost:4200/`
* **Scopes**: `api:read api:write`

**OAuth2 Endpoints:**

* **Authorization URL**: `http://localhost:8080/oauth2/authorize`
* **Token URL**: `http://localhost:8080/oauth2/token`

### Example (Postman Setup)

1. Go to **Authorization** tab → Type: `OAuth 2.0`
2. Grant Type: `Authorization Code`
3. Callback URL: `http://localhost:4200/`
4. Auth URL: `http://localhost:8080/oauth2/authorize`
5. Access Token URL: `http://localhost:8080/oauth2/token`
6. Client ID: `crm-app`
7. Client Secret: `secret`
8. Scope: `api:read api:write`
9. Click **Get New Access Token**

---

## 🏗 Build

```bash
mvn clean install
```

JAR available in `target/` directory.
