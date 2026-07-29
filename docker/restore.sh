#!/usr/bin/env bash
# Восстановление из бэкапа.
# ВНИМАНИЕ: перезапишет текущую БД и uploads.
#
# Запуск из /opt/hansen:
#   ./restore.sh backups/2026-07-28_12-00-00

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

BACKUP_DIR="${1:-}"

if [[ -z "$BACKUP_DIR" ]]; then
  echo "Usage: ./restore.sh backups/YYYY-MM-DD_HH-MM-SS"
  exit 1
fi

if [[ ! -d "$BACKUP_DIR" ]]; then
  echo "Backup folder not found: $BACKUP_DIR"
  exit 1
fi

if [[ ! -f "${BACKUP_DIR}/db.sql.gz" ]]; then
  echo "Missing file: ${BACKUP_DIR}/db.sql.gz"
  exit 1
fi

if [[ ! -f "${BACKUP_DIR}/uploads.tar.gz" ]]; then
  echo "Missing file: ${BACKUP_DIR}/uploads.tar.gz"
  exit 1
fi

if [[ ! -f .env ]]; then
  echo "File .env not found in $SCRIPT_DIR"
  exit 1
fi

set -a
# shellcheck disable=SC1091
source .env
set +a

: "${POSTGRES_USER:?POSTGRES_USER is empty in .env}"
: "${POSTGRES_DB:?POSTGRES_DB is empty in .env}"

echo "WARNING: this will OVERWRITE database (${POSTGRES_DB}) and uploads."
echo "Backup: $BACKUP_DIR"
read -r -p "Type YES to continue: " CONFIRM
if [[ "$CONFIRM" != "YES" ]]; then
  echo "Cancelled."
  exit 1
fi

echo "1/5 Stopping app..."
docker compose stop app

echo "2/5 Recreating PostgreSQL database..."
docker exec -e PGPASSWORD="${POSTGRES_PASSWORD}" postgres-hansen \
  psql -U "${POSTGRES_USER}" -d postgres -v ON_ERROR_STOP=1 <<SQL
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = '${POSTGRES_DB}' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS "${POSTGRES_DB}";
CREATE DATABASE "${POSTGRES_DB}" OWNER "${POSTGRES_USER}";
SQL

echo "3/5 Restoring PostgreSQL dump..."
gunzip -c "${BACKUP_DIR}/db.sql.gz" \
  | docker exec -i -e PGPASSWORD="${POSTGRES_PASSWORD}" postgres-hansen \
    psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" -v ON_ERROR_STOP=1

echo "4/5 Restoring uploads..."
docker run --rm \
  -v uploads_hansen_data:/data \
  -v "$(realpath "$BACKUP_DIR"):/backup:ro" \
  alpine:3.20 \
  sh -c "find /data -mindepth 1 -maxdepth 1 -exec rm -rf {} +; tar xzf /backup/uploads.tar.gz -C /data"

echo "5/5 Starting app..."
docker compose start app

echo "Restore done."
