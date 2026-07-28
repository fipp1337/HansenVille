#!/usr/bin/env bash
# Бэкап PostgreSQL + uploads на VPS.
# Запуск из /opt/hansen:
#   ./backup.sh
#
# Результат:
#   /opt/hansen/backups/YYYY-MM-DD_HH-MM-SS/
#     db.sql.gz
#     uploads.tar.gz
#     meta.txt

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

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

KEEP_DAYS="${BACKUP_KEEP_DAYS:-14}"
STAMP="$(date +%F_%H-%M-%S)"
BACKUP_ROOT="${SCRIPT_DIR}/backups"
BACKUP_DIR="${BACKUP_ROOT}/${STAMP}"

mkdir -p "$BACKUP_DIR"

echo "Backup started: $BACKUP_DIR"

echo "1/3 Dumping PostgreSQL..."
docker exec -e PGPASSWORD="${POSTGRES_PASSWORD}" postgres-hansen \
  pg_dump -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" --no-owner --no-acl \
  | gzip > "${BACKUP_DIR}/db.sql.gz"

echo "2/3 Archiving uploads..."
docker run --rm \
  -v uploads_hansen_data:/data:ro \
  -v "${BACKUP_DIR}:/backup" \
  alpine:3.20 \
  tar czf /backup/uploads.tar.gz -C /data .

echo "3/3 Writing meta..."
{
  echo "created_at=${STAMP}"
  echo "postgres_db=${POSTGRES_DB}"
  echo "postgres_user=${POSTGRES_USER}"
  echo "hostname=$(hostname)"
  echo "db_size_bytes=$(wc -c < "${BACKUP_DIR}/db.sql.gz")"
  echo "uploads_size_bytes=$(wc -c < "${BACKUP_DIR}/uploads.tar.gz")"
} > "${BACKUP_DIR}/meta.txt"

echo "Cleaning backups older than ${KEEP_DAYS} days..."
find "$BACKUP_ROOT" -mindepth 1 -maxdepth 1 -type d -mtime "+${KEEP_DAYS}" -exec rm -rf {} +

echo "Backup done:"
ls -lh "$BACKUP_DIR"
echo "Path: $BACKUP_DIR"
