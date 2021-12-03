--
-- create schema
--
CREATE SCHEMA IF NOT EXISTS sppl;

--
-- Employees
--
CREATE TABLE IF NOT EXISTS sppl.MA_EMPLOYEES (
    id              TEXT PRIMARY KEY,
    firstname       TEXT,
    name            TEXT,
    date_of_birth   TIMESTAMP,
    position        TEXT
);