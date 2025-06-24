@echo off
setlocal EnableDelayedExpansion

set "COMPOSE_FILE=.\docker\docker-compose.yaml"

echo Stopping and removing Docker containers...
docker-compose --env-file .env -f %COMPOSE_FILE% down

endlocal
