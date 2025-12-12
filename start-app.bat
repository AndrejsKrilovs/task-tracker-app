@echo off
setlocal EnableDelayedExpansion

set "COMPOSE_FILE=.\docker\docker-compose.yaml"

echo Pulling image from docker-hub...
docker pull andrejskrilovs/task-tracker-app:4.0

echo Starting application...
docker-compose --env-file .env -f "%COMPOSE_FILE%" up

endlocal
