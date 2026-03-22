# 💳 Fintech Payment System

## 📌 Project Overview

This project is a backend payment processing system built using Spring Boot.  
It simulates real-world digital payment platforms by supporting secure transactions between user accounts.

The system is designed with a strong focus on backend architecture, transaction reliability, and scalability concepts such as idempotency, orchestration, and secure authentication.

---

## 🚀 Key Features

- 🔐 JWT-based Authentication & Authorization
- 🔄 Refresh Token Rotation (secure session management)
- 👤 User with Multiple Accounts
- 🏦 Account Management (create & fetch accounts)
- 💳 Card Management (linked to accounts)
- 💸 Secure Money Transfer between accounts
- 🔁 Idempotency (prevents duplicate transactions)
- 🔄 Transaction Lifecycle (PENDING → SUCCESS / FAILED)
- 📊 Transaction History Tracking

---

## ⚙️ Advanced Backend Features

- 🔐 Stateless Authentication using JWT
- 🔄 Refresh Token Flow for continuous login without re-authentication
- 🧾 Idempotency Key Handling to ensure safe retries
- ⚡ Atomic Transactions using `@Transactional`
- 🧠 Followed Double Entry Accounting method
- 🔍 Implemented Ledger system
- 🔍 Validation and Error Handling for secure operations
- 🔒 Protected APIs using Spring Security filters
- 🧩 Clean layered architecture (Controller → Service → Repository)

---

## 🧱 System Design Highlights

- Separation of concerns (Controller → Service → Repository)
- Idempotent APIs for safe retries
- Transaction state management for reliability
- Designed similar to real-world systems like payment gateways

---

# 🔐 Authentication APIs

## 1. Login

**POST** `/auth/login`

### Request
```json
{
  "email": "test@gmail.com",
  "password": "1234"
}
```
### Response
```json
{
  "token": "JWT_TOKEN",
  "refreshToken": "REFRESH_TOKEN"
}
```
## 2. Refresh Token

**POST** `/auth/refresh`

### Request
```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```
### Response
```json
{
  "token": "NEW_JWT_TOKEN"
}
```

## 3. Create Account

**POST** `/accounts`

### Request
```json
{
  "userId": 6,
  "accountType": "SAVINGS",
  "initialDeposit":1000
}
```









