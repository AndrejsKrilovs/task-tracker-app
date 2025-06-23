#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"

# Извлекаем версию из docker-compose.yaml
APP_VERSION=$(grep -A 10 "task-tracker-app:" "$COMPOSE_FILE" | grep "APP_VERSION" | head -n 1 | sed -E 's/.*APP_VERSION:\s*([0-9.]+).*/\1/')

echo "Detected APP_VERSION: $APP_VERSION"
echo "Cleaning and building project..."
./gradlew clean build

echo "Building and starting containers with version $APP_VERSION..."
docker-compose -f "$COMPOSE_FILE" up --build -d
