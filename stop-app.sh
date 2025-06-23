#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"

echo "Stopping and removing docker containers..."
docker-compose -f "$COMPOSE_FILE" down

# Удаляем dangling images (с тегом <none>)
DANGLING_IMAGES=$(docker images -f "dangling=true" -q || true)

if [[ -n "$DANGLING_IMAGES" ]]; then
  echo "Removing dangling images..."
  echo "$DANGLING_IMAGES" | xargs -r docker rmi -f
else
  echo "No dangling images found."
fi

echo "Done"