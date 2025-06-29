# 📋 Task Tracker App

This is a **full-stack task tracking application** built with [Quarkus](https://quarkus.io/) — the Supersonic Subatomic
Java Framework.

> ✨ It includes REST API, frontend (Vite + Vue 3 + TypeScript), JWT-based authentication, PostgreSQL integration, and
> Swagger
> documentation.

---

## 🚀 Getting Started

🛠️ Before running `start-app`, configure a `.env` file in the project root with the following variables:

```
DATASOURCE_USERNAME=<your imagine user>
DATASOURCE_PASSWORD=<your imagine password>
```

Now you can start the entire app with one command — no manual DB or server setup needed!

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

- Pull application from [docker-hub](https://hub.docker.com/r/andrejskrilovs/task-tracker-app)
- Run PostgreSQL + App containers using Docker Compose
- Apply DB migrations via Liquibase

---

## 🔍 Available Endpoints

| Feature        | URL                                                                                             |
|----------------|-------------------------------------------------------------------------------------------------|
| 🌐 App         | [http://localhost:8080/](http://localhost:8080)                                                 |
| 🔐 Login Page  | [http://localhost:8080/quinoa/login](http://localhost:8080/quinoa/login)                        |
| 📘 Swagger     | [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui)                           |
| 🛠️ DB console | [http://localhost:8888/](http://localhost:8888) (see credentials in docker/docker-compose.yaml) |

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
- **Frontend**: Vite, Vue 3, Pinia, TypeScript, CSS
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
