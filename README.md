# CRM Services

A modular Customer Relationship Management backend application built with Spring Boot 3.5.5 and Java 17. It provides RESTful APIs for managing contacts, products, sales orders, tasks, and user accounts, with support for file storage, email notifications, batch processing, and real-time communication.

## Features

- **Contact Management** -- CRUD operations with pagination, search, and audit history
- **Product Catalog** -- Product lifecycle management with active/inactive status
- **Sales Orders** -- Order creation, status workflow (Created, Approved, Delivered, Canceled), and assignment
- **Sales Order Line Items** -- Per-order item breakdown with quantity, pricing, and discount calculations
- **Task Management** -- Tasks with type, priority, status tracking, due dates, and user assignment
- **Notes / Activity Log** -- Polymorphic notes (Call, Meeting, Email, Note) attachable to contacts or sales orders
- **Dashboard Analytics** -- Aggregated metrics including revenue trends, pipeline summary, and top performers
- **Report Generation** -- Daily sales reports with Excel export stored in MongoDB GridFS
- **File Storage** -- Document uploads via MongoDB GridFS with named collection buckets
- **Email Notifications** -- Automated follow-up reminders for stale orders via Spring Mail
- **Background Processing** -- Spring Batch jobs and scheduled tasks for report generation and email reminders
- **Real-Time Notifications** -- Socket.IO integration for push notifications
- **Authentication and Authorization** -- Spring Authorization Server with OAuth2 and JWT
- **AI Agent Integration** -- BFF proxy to Python AI agent for conversational CRM queries, with internal API endpoints secured by API key
- **API Documentation** -- Swagger/OpenAPI with built-in OAuth2 authentication

## Tech Stack

| Category                | Technology                                 |
|-------------------------|--------------------------------------------|
| **Backend Framework**   | Spring Boot 3.5.5 (Java 17)               |
| **Database (Relational)** | MySQL 8+                                |
| **Database (File Storage)** | MongoDB 8.2.0 (GridFS)               |
| **Security**            | Spring Authorization Server, OAuth2, JWT   |
| **Batch and Scheduling** | Spring Batch, `@Scheduled`                |
| **Real-Time**           | Socket.IO                                 |
| **Email**               | Spring Mail                                |
| **Schema Migration**    | Flyway                                     |
| **Build Tool**          | Maven 3.9                                  |
| **Testing**             | JUnit 5, Mockito                           |
| **API Documentation**   | SpringDoc OpenAPI (Swagger UI)             |

## Architecture Overview

### System Architecture

```
┌───────────────────────────────────────────────────────────────┐
│                       CLIENT LAYER                            │
│              Angular UI / HTTP & WebSocket                    │
└────────────────────────────┬──────────────────────────────────┘
                             │
┌────────────────────────────▼──────────────────────────────────┐
│               SPRING BOOT APPLICATION                         │
│                                                               │
│  ┌─────────────────── SECURITY ───────────────────────────┐   │
│  │  @Order(0) API Key filter (/internal/**)               │   │
│  │  @Order(1) OAuth2 Auth Server                          │   │
│  │  @Order(2) JWT Resource Server ► Form Login            │   │
│  └────────────────────────────────────────────────────────┘   │
│                            │                                  │
│  ┌─────────────────── CONTROLLER ──────────────────────────┐  │
│  │  /api/v1/*      RESTful API (JWT auth, role-based)     │  │
│  │  /api/v1/ai/*   BFF proxy to Python AI agent (JWT)     │  │
│  │  /internal/v1/* Read-only CRM data for agent (API key) │  │
│  └────────────────────────┬────────────────────────────────┘  │
│                            │                                  │
│  ┌─────────────────── SERVICE ─────────────────────────────┐  │
│  │  Business logic, validation, mapping, transactions      │  │
│  │  + Notifications (Socket.IO) + Email + File Storage     │  │
│  │  + AiProxyService (forwards chat to Python agent)       │  │
│  └────────────────────────┬────────────────────────────────┘  │
│                            │                                  │
│  ┌─────────────────── REPOSITORY ──────────────────────────┐  │
│  │  JPA + Native Queries + Projections                     │  │
│  └────────────────────────┬────────────────────────────────┘  │
│                            │                                  │
│  ┌─────────────────── BACKGROUND ──────────────────────────┐  │
│  │  Spring Batch Jobs + @Scheduled Cron Tasks              │  │
│  │  (Report generation, Email reminders)                   │  │
│  └────────────────────────┬────────────────────────────────┘  │
│                            │                                  │
└────────────┬───────────────┼──────────────┬───────────────────┘
             │               │              │
┌────────────▼────────────┐  │  ┌───────────▼──────────────────┐
│       MySQL 8+          │  │  │        MongoDB 8.2.0         │
│  Relational data        │  │  │  File storage (GridFS)       │
│  + Flyway migrations    │  │  │  with named collection       │
│                         │  │  │  buckets                     │
└─────────────────────────┘  │  └──────────────────────────────┘
                             │
              ┌──────────────▼──────────────────┐
              │     Python AI Agent (8000)       │
              │  FastAPI + pydantic-ai + LLM     │
              │  ◄── /internal/v1/* callbacks ──►│
              └─────────────────────────────────┘
```

### Request Flow

```
Client (JWT Bearer Token)
   │
   ▼
Security Filter Chain ──► @Order(2) JWT Resource Server validates token
   │
   ▼
Controller ──► BaseController.getPkUserLogged() extracts user from JWT
   │            @PreAuthorize checks role (ADMIN, USER, STAFF)
   ▼
Service ──► Business logic, validation, entity mapping
   │         Triggers: Notifications (Socket.IO), Emails, File Storage
   ▼
Repository ──► JPA / Native SQL queries
   │
   ▼
MySQL / MongoDB
   │
   ▼
ResponseDTO ──► MappingJacksonValue ──► JSON Response
```

### Background Job Flow

```
@Scheduled Cron Trigger (configurable)
   │
   ▼
Scheduler (DailySalesReportScheduler / EmailReminderScheduler)
   │  Pre-check: skip if already completed
   ▼
BatchJobRunnerService.run(Job)
   │
   ▼
Spring Batch Job ──► Step(s) ──► Reader → Processor → Writer
   │                                              │
   ▼                                              ▼
MySQL (status tracking)              MongoDB GridFS (report files)
                                     Spring Mail (email delivery)
```

### Real-Time Notification Flow

```
Service Layer (e.g., ContactService creates a record)
   │
   ▼
NotificationService.createNotifications()
   │  Persists NotificationMessage records in MySQL
   ▼
SocketEventService.sendNotifications(recipientIds)
   │  Broadcasts UNREAD_COUNT to each user's Socket.IO room
   ▼
Client receives real-time update via WebSocket
```

## Project Structure

```
src/main/java/com/tdbang/crm/
  controllers/      REST API controllers
  services/         Business logic layer
  repositories/     Data access layer (JPA)
  entities/         JPA entity classes
  dtos/             Data transfer objects
  mappers/          Entity-to-DTO mapping
  enums/            Enumerations (status, type, priority)
  config/           Security, OAuth2, Batch, Socket.IO configuration
  batch/            Spring Batch jobs and tasklets
  schedulers/       Scheduled tasks
  exceptions/       Custom exception handling
  utils/            Constants and utilities
```

## API Endpoints

| Module              | Base Path                                | Methods              | Auth     |
|---------------------|------------------------------------------|----------------------|----------|
| Contact             | `/api/v1/contact`                        | GET, POST, PUT, DELETE | JWT    |
| Product             | `/api/v1/product`                        | GET, POST, PUT, DELETE | JWT    |
| Sales Order         | `/api/v1/sales-order`                    | GET, POST, PUT, DELETE | JWT    |
| Sales Order Item    | `/api/v1/sales-order/{orderId}/item`     | GET, POST, PUT, DELETE | JWT    |
| Task                | `/api/v1/task`                           | GET, POST, PUT, DELETE | JWT    |
| Note                | `/api/v1/note`                           | GET, POST, PUT, DELETE | JWT    |
| Dashboard           | `/api/v1/dashboard`                      | GET                  | JWT      |
| Report              | `/api/v1/report`                         | GET                  | JWT      |
| File                | `/api/v1/file`                           | GET, POST, DELETE    | JWT      |
| User                | `/api/v1/user`                           | GET, POST, PUT       | JWT      |
| Notification        | `/api/v1/notification`                   | GET                  | JWT      |
| AI Chat (BFF)       | `/api/v1/ai`                             | POST                 | JWT      |
| Internal API        | `/internal/v1`                           | GET                  | API Key  |

## Business Workflow

The following diagram illustrates the end-to-end CRM business flow, from user authentication through daily operations to automated reporting.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         1. AUTHENTICATION                               │
│                                                                         │
│  User ──► OAuth2 Login ──► JWT Token Issued ──► Access Granted          │
│                                                                         │
└─────────────────────────────────┬───────────────────────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        ▼                         ▼                         ▼
┌───────────────┐     ┌───────────────────┐     ┌───────────────────────┐
│ 2. CONTACTS   │     │ 3. PRODUCTS       │     │ 4. USER MANAGEMENT    │
│               │     │                   │     │                       │
│ Create/Import │     │ Define catalog    │     │ Create accounts       │
│ contacts      │     │ Set pricing       │     │ Assign roles          │
│ Track details │     │ Activate/Deactive │     │ (Admin, User, Staff)  │
└───────┬───────┘     └─────────┬─────────┘     └───────────────────────┘
        │                       │
        └───────────┬───────────┘
                    ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        5. SALES ORDERS                                   │
│                                                                          │
│  Create Order ──► Add Line Items ──► Assign to User ──► Track Status     │
│       │           (Product, Qty,      │                                  │
│       │            Price, Discount)   │                                  │
│       ▼                               ▼                                  │
│  Link to Contact              Status Workflow:                           │
│                               CREATED ► APPROVED ► DELIVERED             │
│                                  │                                       │
│                                  └──► CANCELED                           │
└─────────────────────────────────────────┬────────────────────────────────┘
                                          │
        ┌────────────────┬────────────────┼────────────────┐
        ▼                ▼                ▼                ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
│  6. NOTES    │ │  7. TASKS    │ │  8. FILES    │ │ 9. NOTIFICATIONS │
│              │ │              │ │              │ │                  │
│ Log calls,   │ │ Create TODOs │ │ Attach docs  │ │ Real-time alerts │
│ meetings,    │ │ Set priority │ │ (contracts,  │ │ via Socket.IO    │
│ emails on    │ │ Set due date │ │  invoices)   │ │ on record        │
│ contacts or  │ │ Assign users │ │ Stored in    │ │ changes          │
│ orders       │ │ Track status │ │ MongoDB      │ │                  │
│              │ │ OPEN ► IN    │ │ GridFS       │ │                  │
│              │ │ PROGRESS ►   │ │              │ │                  │
│              │ │ DONE         │ │              │ │                  │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────────┘
                                          │
                                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    10. AUTOMATED BACKGROUND JOBS                        │
│                                                                         │
│  ┌─────────────────────────────┐  ┌──────────────────────────────────┐  │
│  │   Daily Sales Report        │  │   Email Reminders                │  │
│  │                             │  │                                  │  │
│  │   Aggregate daily orders    │  │   Detect stale contacts/orders   │  │
│  │   Generate Excel report     │  │   Send follow-up reminders       │  │
│  │   Store in MongoDB GridFS   │  │   to assigned users via email    │  │
│  └─────────────────────────────┘  └──────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────┬───────────────────────────────────┘
                                      ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      11. DASHBOARD & REPORTING                           │
│                                                                          │
│  Revenue Trends ── Pipeline Summary ── Top Performers ── Download Reports│
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

## AI Agent Integration

This application acts as a **BFF (Backend-for-Frontend)** proxy between the Angular UI and a Python AI agent (`crm-agent`). The agent provides a conversational interface for querying CRM data using natural language.

### How It Works

```text
Angular UI ──(JWT)──► /api/v1/ai/chat ──(X-API-Key)──► Python Agent (port 8000)
                                                              │
                                                              ▼
                      /internal/v1/* ◄──(X-API-Key)── Agent tool calls
```

1. **UI sends chat** to `POST /api/v1/ai/chat` with JWT auth and `{sessionId, message}`
2. **AiProxyService** resolves user context from JWT, forwards to the Python agent
3. **Python agent** processes the message with an LLM, calling CRM tools as needed
4. **Agent tools** call back to `/internal/v1/*` endpoints to fetch CRM data
5. **Response** flows back through the proxy to the UI

### Internal API Endpoints (`/internal/v1/*`)

All endpoints are **GET-only**, secured by API key (`X-API-Key` header), hidden from Swagger, and serve the Python agent's tool calls:

| Endpoint | Data |
| -------- | ---- |
| `/internal/v1/dashboard/pipeline-summary` | Order counts by status |
| `/internal/v1/dashboard/revenue-trend?months=12` | Monthly revenue data |
| `/internal/v1/dashboard/top-users?limit=5` | Top salespeople by revenue |
| `/internal/v1/reports?from=...&to=...` | Reports in date range |
| `/internal/v1/tasks/summary?userPk=...` | Task counts by status for user |
| `/internal/v1/contacts?contactName=...` | Contact search (paginated) |
| `/internal/v1/contacts/{id}` | Contact details |
| `/internal/v1/sales-orders?subject=...` | Order search (paginated) |
| `/internal/v1/sales-orders/{id}` | Order details |
| `/internal/v1/sales-orders/count/status` | Order count by status |
| `/internal/v1/sales-orders/{orderId}/items` | Order line items |
| `/internal/v1/notes?entityType=...&entityFk=...` | Notes for an entity |
| `/internal/v1/products?pageNumber=0&pageSize=50` | Product catalog |

### Configuration

Properties in `crm-services.properties`:

```properties
ai.agent.base-url=http://localhost:8000
ai.agent.api-key=crm-agent-secret-key-change-me
ai.agent.timeout-ms=60000
```

### Security

- `/internal/**` uses a dedicated `@Order(0)` security filter chain (stateless, CSRF disabled)
- `ApiKeyAuthFilter` compares `X-API-Key` header to `ai.agent.api-key` property
- Authenticated requests receive `ROLE_AGENT` authority
- Existing `/api/v1/*` JWT endpoints are unaffected

## Getting Started

Refer to [HELP.md](HELP.md) for environment setup, installation, running the application, data population, authorization configuration, and API documentation details.

## License

Copyright 2025-2026 tdbang. All rights reserved.
