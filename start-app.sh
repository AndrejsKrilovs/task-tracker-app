#!/usr/bin/env bash
set -euo pipefail

# Always run from repo root (directory where this script lives)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

ENV_FILE=".env"
COMPOSE_FILE="./docker/docker-compose.yaml"

echo "Building Quarkus app..."
./gradlew clean build

echo "Starting application (docker compose)..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up --build