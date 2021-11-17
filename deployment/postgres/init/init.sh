#!/bin/bash

set -e

psql --username "${POSTGRES_USER}" --dbname "${POSTGRES_DB}" <<-EOSQL
    CREATE DATABASE brewery
EOSQL

ls -al /var/lib/postgresql/data/ddl

for file in /var/lib/postgresql/data/ddl/*.ddl.sql
do
  echo "Applying ${file}"

  psql \
    --username "${POSTGRES_USER}" \
    --dbname "brewery" \
    -a -f ${file}
done
