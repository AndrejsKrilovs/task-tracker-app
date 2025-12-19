@echo off
setlocal

set "ENV_FILE=.env"
set "COMPOSE_FILE=.\docker\docker-compose.yaml"

echo Building Quarkus app...
call .\gradlew clean build || exit /b 1

echo Starting Docker Compose...
docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" up --build

endlocal
