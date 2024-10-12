#!/bin/bash

OS_TYPE=$(uname)
SCRIPT_PATH=$(realpath "$0")
SCRIPT_DIR=$(dirname "$SCRIPT_PATH")
ROOT_DIR=$(dirname "$SCRIPT_DIR")

# 리눅스 환경일 경우
if [[ "$OS_TYPE" == "Linux" ]]; then
    echo "Detected Linux environment."

    if ! systemctl is-active --quiet docker; then
        echo "Docker is not running. Starting Docker..."
        sudo systemctl start docker  # Docker Daemon 시작

        while ! docker info > /dev/null 2>&1; do
            echo "Waiting for Docker to start..."
            sleep 5
        done
    else
        echo "Docker is already running."
    fi
# Windows (Git Bash) 환경일 경우
elif [[ "$OS_TYPE" =~ "MINGW" || "$OS_TYPE" =~ "MSYS" ]]; then
    echo "Detected Windows (Git Bash) environment."

    if ! docker info > /dev/null 2>&1; then
        echo "Docker Desktop is not running. Starting Docker Desktop..."
        powershell.exe -Command "Start-Process 'C:\Program Files\Docker\Docker\Docker Desktop.exe'"

        while ! docker info > /dev/null 2>&1; do
            echo "Waiting for Docker Desktop to start..."
            sleep 5
        done
    else
        echo "Docker is already running."
    fi
else
    echo "Unsupported operating system: $OS_TYPE"
    exit 1
fi

# Docker Desktop이 실행되었으면 docker-compose 실행
docker-compose down
echo "Docker down."
rm -rf "$ROOT_DIR/docker/data/kafka_data"
docker-compose --env-file "$ROOT_DIR/.env" up -d
echo "Docker up complete."