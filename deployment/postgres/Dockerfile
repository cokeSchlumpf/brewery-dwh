FROM postgres:14

COPY ddl /var/lib/postgresql/data/ddl
COPY init/init.sh /docker-entrypoint-initdb.d/init.sh

RUN chmod +x /docker-entrypoint-initdb.d/init.sh
