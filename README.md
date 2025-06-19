# 📋 Task Tracker App

This is a **full-stack task tracking application** built with [Quarkus](https://quarkus.io/) — the Supersonic Subatomic
Java Framework.

> ✨ It includes REST API, frontend (Vite + Vue 3 + TypeScript), JWT-based authentication, PostgreSQL integration, and
> Swagger
> documentation.

---

## 🚀 Getting Started

### 🔧 Prerequisites

- [Java 21+](https://jdk.java.net/)
- [Docker](https://www.docker.com/)
- [Node.js (auto-installed via Gradle plugin)](https://nodejs.org/)
- Git (optional)

---

## 📦 Build & Run the App

You can start the entire app with one command — no manual DB or server setup needed!

### ▶️ Windows

```
start-app.bat
```

### ▶️ Linux / macOS

```
chmod +x start-app.sh stop-app.sh
./start-app.sh
```

✅ This will:

- Build backend & frontend
- Run PostgreSQL + App containers using Docker Compose
- Apply DB migrations via Liquibase

---

## 🔍 Available Endpoints

| Feature       | URL                                  |
|---------------|--------------------------------------|
| 🌐 App        | `http://localhost:8080/`             |
| 🔐 Login Page | `http://localhost:8080/quinoa/login` |
| 📘 Swagger    | `http://localhost:8080/swagger-ui/`  |

---

## 🛑 Stop the App

### Windows

```
stop-app.bat
```

### Linux / macOS

```
./stop-app.sh
```

---

## 💡 Technologies Used

- **Backend**: Quarkus, Hibernate, Liquibase, JWT
- **Frontend**: Vite, Vue 3, TypeScript, CSS
- **Database**: PostgreSQL
- **Tests**: JUnit 5, RestAssured, Mockito
- **CI/Build**: Gradle, Jacoco, Docker

---

## 🤝 Author

Made with ❤️ by **Andrejs Krilovs**  
Happy usage and contributions welcome!

📬 Contact me:
- Telegram: [@andrejs_krilovs](https://t.me/andrejs_krilovs)
- WhatsApp: [+371 24770033](https://wa.me/37124770033)
