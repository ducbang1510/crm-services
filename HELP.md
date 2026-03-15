# CRM Services -- Setup and Reference Guide

## Table of Contents

- [Required Installations](#required-installations)
- [Getting Started](#getting-started)
- [Populate Data](#populate-data)
- [Authorization (OAuth2)](#authorization-oauth2)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Reference Documentation](#reference-documentation)
- [Maven Notes](#maven-notes)

---

## Required Installations

The following instructions are for Windows OS.

### Java 17

1. Download JDK 17 from [Oracle JDK 17 Archive](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) -- choose the Windows x64 Installer.
2. Install the JDK.
3. Set up the `JAVA_HOME` environment variable:
   1. Open **Settings > System > About > Advanced system settings > Environment Variables**.
   2. Add a new variable: `JAVA_HOME` = path to JDK 17 (e.g., `C:\Program Files\Java\jdk-17`).
   3. Edit the `Path` variable and add `%JAVA_HOME%\bin;`.
   4. Verify by running `java --version` in a terminal.

### Maven 3.9+

1. Download Maven 3.9 from [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) -- choose the Binary zip archive.
2. Extract the zip and copy the path to the `bin` folder (e.g., `C:\Program Files\Maven\apache-maven-3.9.9\bin`).
3. Add the path to the `Path` environment variable.
4. Verify by running `mvn -v` in a terminal.

### MySQL 8+

1. Download MySQL from [dev.mysql.com/downloads/installer](https://dev.mysql.com/downloads/installer/) -- choose `mysql-installer-community-8+.msi` (not the web version).
2. Follow the installation guide at [w3schools.com/mysql/mysql_install_windows.asp](https://www.w3schools.com/mysql/mysql_install_windows.asp).
3. Choose the **Full** installation to include MySQL Server and MySQL Workbench/Shell.
4. Enable the option to auto-start the `MySQL80` service with Windows.

### MongoDB 8.2.0

1. Download MongoDB Community Server from [mongodb.com/try/download/community-kubernetes-operator](https://www.mongodb.com/try/download/community-kubernetes-operator) -- keep the default settings and download.
2. During installation, click **Complete** when prompted for setup type.
3. Proceed through the installer with default settings.
4. Add MongoDB's `bin` folder to the `Path` environment variable (e.g., `C:\Program Files\MongoDB\Server\<version>\bin`).
5. Verify by running `mongod --version` in a terminal.

### MailDev (Optional -- for local email testing)

If you want to test email notifications locally, install MailDev:

```sh
npm install -g maildev
maildev
```

MailDev provides a local SMTP server and a web UI to view sent emails.

---

## Getting Started

### 1. Create the MySQL Schema

```sql
CREATE SCHEMA IF NOT EXISTS crm_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

### 2. Build the Application

```sh
cd crm-services
mvn clean install
```

### 3. Configure the Application

Copy the properties file to the `target` folder and update database credentials:

```sh
cd target
cp ../src/main/resources/crm-services.properties
```

> On Windows, use `copy` instead of `cp`.

Edit `crm-services.properties` and update:

```properties
spring.datasource.username=[YOUR_MYSQL_USERNAME]
spring.datasource.password=[YOUR_MYSQL_PASSWORD]
```

### 4. Start the Application

```sh
java -jar crm-services-<version>-SNAPSHOT.jar
```

The application runs on port **8080**. Base API URL: `http://localhost:8080/api/v1`

### Quick Start Script (Windows)

A helper script `start_local.bat` is included in the project root. It automates the build, configuration copy, MailDev startup, port cleanup, and application launch.

1. Update database credentials in `crm-services.properties` (see step 3 above).
2. Open `start_local.bat` and set your JDK 17 path and MySQL connection info.
3. Run the script from the project root:

   ```sh
   start_local.bat
   ```

4. Choose an option when prompted:
   - **[1] Start** -- Use the existing JAR in the `target` folder.
   - **[2] Build & Start** -- Run `mvn clean install` first, then start the app.

---

## Populate Data

1. Run the SQL script `populate_data.sql` against the `crm_db` schema to insert sample data.
2. Log in with any user from the database using the default password:

   ```
   Username: any username in the database
   Password: 123456
   ```

---

## Authorization (OAuth2)

The application uses Spring Authorization Server with the OAuth2 Authorization Code grant.

### Server Endpoints

| Endpoint              | Description           |
|-----------------------|-----------------------|
| `/oauth2/authorize`   | Authorization         |
| `/oauth2/token`       | Token                 |
| `/oauth2/jwks`        | JSON Web Key Set      |
| `/oauth2/introspect`  | Token introspection   |
| `/oauth2/logout`      | Logout                |

### Client Configuration

| Property         | UI Client (`crm-app`)    | Swagger Client (`swagger-ui`) |
|------------------|--------------------------|-------------------------------|
| **Client ID**    | `crm-app`                | `swagger-ui`                  |
| **Client Secret**| `secret`                 | `swagger-secret`              |
| **Redirect URI** | `http://localhost:4200`  | Swagger callback              |
| **Scopes**       | `api:read api:write openid profile` | `api:read api:write openid profile` |

### Postman Setup

1. Go to the **Authorization** tab and set Type to **OAuth 2.0**.
2. Configure:
   - **Grant Type**: Authorization Code
   - **Callback URL**: `http://localhost:4200`
   - **Auth URL**: `http://localhost:8080/oauth2/authorize`
   - **Access Token URL**: `http://localhost:8080/oauth2/token`
   - **Client ID**: `crm-app`
   - **Client Secret**: `secret`
   - **Scope**: `api:read api:write openid profile`
3. Click **Get New Access Token**.

---

## API Documentation (Swagger)

| Resource        | URL                                        |
|-----------------|--------------------------------------------|
| **Swagger UI**  | `http://localhost:8080/swagger-ui.html`     |
| **OpenAPI JSON**| `http://localhost:8080/v3/api-docs`         |

To authenticate in Swagger UI, click the **Authorize** button and enter the Swagger client credentials (`swagger-ui` / `swagger-secret`).

---

## Reference Documentation

- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/3.5.5/maven-plugin)
- [Spring Web](https://docs.spring.io/spring-boot/3.5.5/reference/web/servlet.html)
- [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
- [Flyway Migration](https://docs.spring.io/spring-boot/3.5.5/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
- [Spring Security](https://docs.spring.io/spring-boot/3.5.5/reference/web/spring-security.html)
- [Validation](https://docs.spring.io/spring-boot/3.5.5/reference/io/validation.html)
- [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)

---

## Maven Notes

Due to Maven's design, elements are inherited from the parent POM to the project POM. While most inheritance is appropriate, it also inherits unwanted elements like `<license>` and `<developers>` from the parent. The project POM contains empty overrides for these elements. If you switch to a different parent and want the inheritance, remove those overrides.
