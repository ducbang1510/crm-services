# CRM Project 🚀

A lightweight, modular **Customer Relationship Management (CRM)** web application built with **Spring Boot 3.5.5** and
**Java 17**. It provides a foundation for managing contacts, products, sales orders, and user accounts, etc. — with
built-in
support for file uploads, email notifications, and real-time communication.

UI repository built with Angular for integration: https://github.com/luuthanhvan/CRM-new-version.git

---

## ✨ Features

* 🧱 Customer management (RESTful APIs)
* ✉️ Email Notifications (Spring Mail)
* 📂 File Uploads (MongoDB + GridFS)
* ⏰ Background Tasks & Scheduling (Spring Batch / Cron)
* 🔔 Real-Time Notifications (Socket.IO)
* 🔐 Authentication & Authorization (OAuth2)
* 🗃️ Database Design
  * MySQL (Relational Data)
  * MongoDB (File Storage)
* 📑Swagger-powered documentation

---

## 🛠 Tech Stack

| Category                          | Technology                                     |
|-----------------------------------|------------------------------------------------|
| **Backend Framework**             | Spring Boot 3.5 (Java 17)                      |
| **Database (Relational)**         | MySQL 8+                                       |
| **Database (Binary/File)**        | MongoDB 8.2.0 (GridFS)                         |
| **Messaging / Real-Time**         | Socket.IO                                      |
| **Email**                         | Spring Mail (Outlook SMTP / OAuth2)            |
| **Build Tool**                    | Maven                                          |
| **Security**                      | Spring Security with Spring Boot Oauth2 server |
| **Batch & Scheduling** (Incoming) | Spring Batch, `@Scheduled`                     |
| **Testing**  (Incoming)           | JUnit 5 / Postman Collections                  |

---

## 🧪 Getting Started

### 1️⃣ Prerequisites

Please refer to `HELP.md` file for installing required items

### 2️⃣ Run local app

1. Create new schema in MySQL

    ```sh
    CREATE SCHEMA IF NOT EXISTS crm_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    ```

2. Build the jar file

    ```sh
    cd crm-services
    mvn clean install
    ```

3. Copy properties files to target folder and update configuration

    ```sh
    cd target
    cp ..\src\main\resources\crm-services.properties (use 'copy' instead of 'cp' for Windows os)
    ```

   Update `crm-services.properties` file

    ```properties
    spring.datasource.username=[USERNAME_MYSQL]
    spring.datasource.password=[PASSWORD_MYSQL]
    ```

4. Start application

    ```sh
    java -jar crm-services-<version>-SNAPSHOT.jar
    ```

App runs at port 8080, Base API url **[http://localhost:8080/api/v1](http://localhost:8080/api/v1)**

### 3️⃣ Populate Data and Quick Experience

1. Populate data by `populate_data.sql` file

2. Enjoy with any users, credential below:

```text
username: any username in db
password: 123456
```

---

## 🔐 Authorization

OAuth2 Authorization Server endpoints:

* `/oauth2/authorize` – Authorization endpoint
* `/oauth2/token` – Token endpoint
* `/oauth2/jwks` – JWKS endpoint
* `/oauth2/introspect` – Token introspection

### 🔑 Sample OAuth2 Client Config (Authorization Code Grant)

**Client Info (for testing):**

* **Client ID**: `crm-app` (for UI) and `swagger-ui` (for swagger)
* **Client Secret**: `secret` (for UI) and `swagger-secret` (for swagger)
* **Redirect URI**: `http://localhost:4200`
* **Scopes**: `api:read api:write openid profile`

### Example (Postman Setup)

1. Go to **Authorization** tab → Type: `OAuth 2.0`
2. Grant Type: `Authorization Code`
3. Callback URL: `http://localhost:4200`
4. Auth URL: `http://localhost:8080/oauth2/authorize`
5. Access Token URL: `http://localhost:8080/oauth2/token`
6. Client ID: `crm-app`
7. Client Secret: `secret`
8. Scope: `api:read api:write openid profile`
9. Click **Get New Access Token**

---

## 📑 API Documentation

* Swagger UI → **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
* OpenAPI JSON → **[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)**
* The Swagger is included Authentication configuration, use button 'Authorize' and input above *Client ID/Client Secret*
  to login
