@echo off
setlocal enabledelayedexpansion

set "COMPOSE_FILE=.\docker\docker-compose.yaml"

echo Stopping and removing Docker containers...
docker-compose -f %COMPOSE_FILE% down
echo Done.

endlocal
