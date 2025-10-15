# Order Processor Service

A Spring Boot backend application for E-commerce Order Management, supporting order creation, retrieval, cancellation, and status tracking. The project uses MySQL as the database

---

## Table of Contents

- [Features](#features)  
- [Tech Stack](#tech-stack)  
- [Database Schema](#database-schema)  
- [API Endpoints](#api-endpoints)  
- [Pagination](#pagination)  
- [Exception Handling](#exception-handling)  
- [Swagger Documentation](#swagger-documentation)  
- [Testing](#testing)  
- [Setup](#setup)  

---

## Features

- Create an order with multiple items.  
- Retrieve order details by order ID.  
- List all orders with optional filtering by status.  
- Cancel an order (only if it is in PENDING status).  
- Automatically update order statuses using background jobs.  
- Pagination support for listing orders.   

---

## Tech Stack

- Java 17  
- Spring Boot 3.x  
- Spring Data JPA  
- MySQL  
- Lombok  
- Spring Validation   
- JUnit 5 + Mockito for unit tests  

---

## Database Schema

### Tables

**orders**

| Column       | Type     | Description                             |
| ------------ | -------- | --------------------------------------- |
| id           | BIGINT   | Primary key                             |
| customer_id  | BIGINT   | ID of the customer                       |
| total_amount | DECIMAL  | Total amount of the order               |
| status       | VARCHAR  | Order status (PENDING, PROCESSING, etc)|
| created_at   | DATETIME | Auto-generated order creation timestamp |
| updated_at   | DATETIME | Auto-generated last update timestamp    |
| cancelled_at | DATETIME | Timestamp when order was cancelled       |

**order_items**

| Column       | Type    | Description                  |
| ------------ | ------- | ---------------------------- |
| id           | BIGINT  | Primary key                 |
| order_id     | BIGINT  | Foreign key to orders       |
| product_id   | BIGINT  | Product ID                  |
| product_name | VARCHAR | Name of the product         |
| quantity     | INT     | Quantity ordered            |
| price        | DECIMAL | Price per item              |

---

## API Endpoints
### 1. Create Order

POST /api/order
Headers: X-Customer-Id: <customerId>
Body:
{
"items": [
{ "productId": 1, "productName": "Laptop", "quantity": 1, "price": 1000.00 }
]
}
Response: 201 Created

### 2. Get Order by ID
GET /api/order/{orderId}
Response: 200 OK

### 3. Get All Orders

GET /api/order
Query Params:

status (optional): PENDING | PROCESSING | SHIPPED | DELIVERED | CANCELLED

page (default: 0)

size (default: 10)
Response: 200 OK

### 4. Cancel Order
DELETE /api/order/{orderId}
Headers: X-Customer-Id: <customerId>
Response: 200 OK

**Testing**

Unit tests with JUnit 5 and Mockito

Controller, Service, and Exception handling tests included

Code coverage reports available via JaCoCo

**Prerequisites**

Java 17+

Maven

MySQL

