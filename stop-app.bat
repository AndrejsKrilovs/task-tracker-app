@echo off
setlocal enabledelayedexpansion

set "COMPOSE_FILE=.\docker\docker-compose.yaml"

echo Stopping and removing Docker containers...
docker-compose -f %COMPOSE_FILE% down

:: Удаление dangling образов (с тегом <none>)
for /f %%I in ('docker images -f "dangling=true" -q') do (
    echo Removing dangling image: %%I
    docker rmi -f %%I
)

echo Done.

endlocal
