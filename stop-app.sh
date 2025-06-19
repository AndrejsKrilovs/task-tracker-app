#!/bin/bash
echo "Stopping and removing docker containers..."
docker-compose -f ./docker/docker-compose.yaml down
