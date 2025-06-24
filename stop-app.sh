#!/bin/bash

set -e

COMPOSE_FILE="./docker/docker-compose.yaml"

echo "Stopping and removing docker containers..."
docker-compose --env-file .env -f "$COMPOSE_FILE" down

echo "Done"