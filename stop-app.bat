@echo off
setlocal enabledelayedexpansion

set "COMPOSE_FILE=.\docker\docker-compose.yaml"
set "IMAGE_BASE_NAME=task-tracker-app"

echo Stopping and removing Docker containers...
docker-compose -f %COMPOSE_FILE% down

echo Checking for existing images of %IMAGE_BASE_NAME%...
FOR /F "tokens=*" %%I IN ('docker images %IMAGE_BASE_NAME% --format "{{.Repository}}:{{.Tag}}"') DO (
    set "IMAGE=%%I"
    echo Found image: !IMAGE!
    echo Removing image: !IMAGE!
    docker rmi -f !IMAGE!
)

echo Done.

endlocal
