#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"
IMAGE_BASE_NAME="task-tracker-app"

# Извлекаем версию из docker-compose.yaml
APP_VERSION=$(grep -A 10 "$IMAGE_BASE_NAME:" "$COMPOSE_FILE" | grep "APP_VERSION" | head -n 1 | sed -E 's/.*APP_VERSION:\s*([0-9.]+).*/\1/')

echo "Detected APP_VERSION: $APP_VERSION"
echo "Cleaning and building project..."
./gradlew clean build

# Удаляем старые образы, кроме текущей версии
EXISTING_IMAGES=$(docker images "$IMAGE_BASE_NAME" --format "{{.Repository}}:{{.Tag}}" | grep -v ":$APP_VERSION" || true)

if [[ -n "$EXISTING_IMAGES" ]]; then
  echo "Removing old images (excluding version $APP_VERSION):"
  echo "$EXISTING_IMAGES" | xargs -r docker rmi -f
else
  echo "No outdated images found for $IMAGE_BASE_NAME."
fi

# Получаем volume'ы из docker-compose.yaml
COMPOSE_VOLUMES=$(docker-compose -f "$COMPOSE_FILE" config --volumes)

# Удаляем неименованные (анонимные) тома, кроме задекларированных
for VOLUME in $(docker volume ls -qf "dangling=true"); do
  if ! echo "$COMPOSE_VOLUMES" | grep -q "$VOLUME"; then
    echo "Removing unnamed volume: $VOLUME"
    docker volume rm "$VOLUME"
  fi
done

echo "Building and starting containers with version $APP_VERSION..."
docker-compose -f "$COMPOSE_FILE" up --build -d
