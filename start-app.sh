#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"
IMAGE_BASE_NAME="task-tracker-app"

# Извлекаем версию из docker-compose.yaml
APP_VERSION=$(grep -A 10 "$IMAGE_BASE_NAME:" "$COMPOSE_FILE" | grep "APP_VERSION" | head -n 1 | sed -E 's/.*APP_VERSION:\s*([0-9.]+).*/\1/' | tr -d '"')

echo "Detected APP_VERSION: $APP_VERSION"
echo "Cleaning and building project..."
./gradlew clean build

echo "Building and starting containers with version $APP_VERSION..."
docker-compose --env-file .env -f "$COMPOSE_FILE" up --build -d

echo "📦 Removing dangling images..."
docker images -f "dangling=true" -q | xargs -r docker rmi -f

# Перечисли известные тома из docker-compose
declare -A KEEP_VOLUMES=(
  [db-volumes]=1
  [app-volumes]=1
)

echo "🧹 Cleaning up Docker volumes (excluding known ones)..."
docker volume ls -q | while read -r volume; do
  if [[ -z "${KEEP_VOLUMES[$volume]}" ]]; then
    echo "Removing volume: $volume"
    docker volume rm "$volume" > /dev/null
  fi
done
