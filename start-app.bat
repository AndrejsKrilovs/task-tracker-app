@echo off
setlocal EnableDelayedExpansion

set "COMPOSE_FILE=.\docker\docker-compose.yaml"
set "IMAGE_BASE_NAME=task-tracker-app"
set "APP_VERSION="

:: Извлечь версию из docker-compose.yaml
for /f "usebackq tokens=1,* delims=:" %%A in (`findstr /R /C:"APP_VERSION:" "%COMPOSE_FILE%"`) do (
    set "APP_VERSION=%%B"
    set "APP_VERSION=!APP_VERSION: =!"
)

echo Detected APP_VERSION: %APP_VERSION%

echo Cleaning and building project...
call .\gradlew clean build

:: Удалить все старые образы с именем task-tracker-app:*
FOR /f %%i IN ('docker images %IMAGE_BASE_NAME% --format "%%repository%%:%%tag%%"') DO (
    echo Found existing image: %%i
    docker rmi -f %%i
)

echo Building and starting containers with version %APP_VERSION%...
docker-compose -f "%COMPOSE_FILE%" up --build -d

endlocal
