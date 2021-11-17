# Deployment

This deployment contains a PostgreSQL database and a pgAdmin to manage the database. To run the deployment, first copy DDL-files into the related directory:

```bash
$ cp ./models/*.ddl.sql ./deployment/postgres/ddl/
```

Then you can run the Docker Compose setup:

```bash
$ docker compose up -d
# or
$ docker-compose up -d
```

PgAdmin will be available on http://localhost:8080. Username is `fsdi@kpmg.ch`, password `password`. On first startup you need to add the database to the PgAdmin instance. The database connection is `postgres:password@postgres`.