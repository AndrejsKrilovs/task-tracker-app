@echo off
setlocal EnableDelayedExpansion

set "ENV_FILE=.env"
set "COMPOSE_FILE=.\docker\docker-compose.yaml"

echo Shutting down application...
docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" down

endlocal
