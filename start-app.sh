#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"

# Извлекаем версию из docker-compose.yaml
APP_VERSION=$(grep -A 10 "task-tracker-app:" "$COMPOSE_FILE" | grep "APP_VERSION" | head -n 1 | sed -E 's/.*APP_VERSION:\s*([0-9.]+).*/\1/')
IMAGE_BASE_NAME="task-tracker-app"

echo "Detected APP_VERSION: $APP_VERSION"
echo "Cleaning and building project..."
./gradlew clean build

# Удаляем все старые образы с именем task-tracker-app:<любая версия>
EXISTING_IMAGES=$(docker images "$IMAGE_BASE_NAME" --format "{{.Repository}}:{{.Tag}}")

if [[ -n "$EXISTING_IMAGES" ]]; then
  echo "Found existing images:"
  echo "$EXISTING_IMAGES"

  echo "Removing all existing images..."
  echo "$EXISTING_IMAGES" | xargs -r docker rmi -f
else
  echo "No existing images found for $IMAGE_BASE_NAME."
fi

echo "Building and starting containers with version $APP_VERSION..."
docker-compose -f "$COMPOSE_FILE" up --build -d
