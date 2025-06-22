#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"
IMAGE_BASE_NAME="task-tracker-app"

echo "Stopping and removing docker containers..."
docker-compose -f "$COMPOSE_FILE" down

echo "Checking for existing images of $IMAGE_BASE_NAME..."
EXISTING_IMAGES=$(docker images "$IMAGE_BASE_NAME" --format "{{.Repository}}:{{.Tag}}")

if [[ -n "$EXISTING_IMAGES" ]]; then
  echo "Found images:"
  echo "$EXISTING_IMAGES"

  echo "Removing all images of $IMAGE_BASE_NAME..."
  echo "$EXISTING_IMAGES" | xargs -r docker rmi -f
else
  echo "No images found for $IMAGE_BASE_NAME."
fi
