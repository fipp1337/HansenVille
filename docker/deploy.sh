#!/usr/bin/env bash
# Деплой Hansen App на VPS (всегда latest).
#
# На компе:
#   ./mvnw -DskipTests package
#   docker build -t hansen-app:latest .
#   docker save hansen-app:latest -o hansen-app.tar
#   scp hansen-app.tar user@vps:/opt/hansen/
#
# На сервере:
#   cd /opt/hansen
#   ./deploy.sh

set -euo pipefail

TAR_FILE="${1:-hansen-app.tar}"

if [[ ! -f "$TAR_FILE" ]]; then
  echo "File not found: $TAR_FILE"
  echo "Usage: ./deploy.sh [hansen-app.tar]"
  exit 1
fi

echo "==> Loading image from $TAR_FILE"
docker load -i "$TAR_FILE"

echo "==> Starting / updating stack"
docker compose up -d --force-recreate app

echo "==> Status"
docker compose ps

echo "Done."
