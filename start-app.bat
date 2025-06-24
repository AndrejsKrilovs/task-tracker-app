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

echo Building and starting containers with version %APP_VERSION%...
docker-compose --env-file .env -f "%COMPOSE_FILE%" up --build -d

:: Удаление dangling образов (с тегом <none>)
echo Removing dangling images...
for /f %%I in ('docker images -f "dangling=true" -q') do (
    echo Removing dangling image: %%I
    docker rmi -f %%I
)

endlocal
