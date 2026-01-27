#!/bin/bash
SECRETS_FILE="./secrets.env"
if [ -f ${SECRETS_FILE} ]; then
  export $(cat ${SECRETS_FILE} | grep -v '^#' | xargs)
else
  echo "Файл ${SECRETS_FILE} не найден"
  read -p "Нажмите Enter для завершения..."
  exit 1
fi

TIMESTAMP=$(date +"%Y-%m-%d-%H-%M-%S")
BACKUP_DIR="./backup/postgresql"
mkdir -p "$BACKUP_DIR"

BACKUP_FILE="${BACKUP_DIR}/${PG_DATABASE}_${TIMESTAMP}.sql"

export PGPASSWORD="${PG_PASSWORD}"

pg_dump -U "$PG_USER" -h "$PG_HOST" "$PG_DATABASE" 2>error.log > "$BACKUP_FILE"
if [ $? -eq 0 ]; then
  echo "Бэкап успешно создан: $BACKUP_FILE"
  read -p "Нажмите Enter для завершения..."
  gzip "$BACKUP_FILE"
  if [ $? -eq 0 ]; then
    echo "Бэкап успешно сжат: ${BACKUP_FILE}.gz"
    read -p "Нажмите Enter для завершения..."
  else
    echo "Ошибка при сжатии бэкапа"
    read -p "Нажмите Enter для завершения..."
    exit 1
  fi
else
  echo "Ошибка при создании бэкапа"
  read -p "Нажмите Enter для завершения..."
  exit 1;
fi
