@echo off
echo Cleaning and building project without tests...
echo Tests can be include after separate test database implementation.
call gradlew clean build -x test

echo Starting docker containers...
docker-compose -f .\docker\docker-compose.yaml up --build
