#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

ENV_FILE=".env"
COMPOSE_FILE="./docker/docker-compose.yaml"

echo "Stopping and removing docker containers..."
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" down

echo "Done"