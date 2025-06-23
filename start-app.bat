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

echo Detected APP_VERSION: !APP_VERSION!
echo Cleaning and building project...
call .\gradlew clean build

:: Удаление старых образов, кроме текущей версии
for /f "tokens=1,2 delims=:" %%I in ('docker images %IMAGE_BASE_NAME% --format "%%.Repository%%:%%.Tag%%"') do (
    echo Checking image: %%I:%%J
    if not "%%J"=="!APP_VERSION!" (
        echo Removing image %%I:%%J...
        docker rmi -f %%I:%%J
    )
)

:: Получение списка именованных volumes из docker-compose.yaml
echo Getting volumes from compose file...
for /f "tokens=* delims=" %%V in ('docker-compose -f "%COMPOSE_FILE%" config --volumes') do (
    set "KNOWN_VOLUME_%%V=1"
)

:: Удаление анонимных томов, не присутствующих в compose
for /f %%V in ('docker volume ls -qf "dangling=true"') do (
    set "FOUND=0"
    set "VAR=KNOWN_VOLUME_%%V"
    call set FOUND=%%%VAR%%%
    if not defined FOUND (
        echo Removing unnamed volume: %%V
        docker volume rm %%V
    )
)

echo Building and starting containers with version !APP_VERSION!...
docker-compose -f "%COMPOSE_FILE%" up --build -d

endlocal
pause
