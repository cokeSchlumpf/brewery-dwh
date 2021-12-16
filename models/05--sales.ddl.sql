--
-- create schema
--
CREATE SCHEMA IF NOT EXISTS sppl;

--
-- Customers
--

CREATE SEQUENCE IF NOT EXISTS sppl.CUSTOMER_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.CUSTOMERS (
    id          INTEGER PRIMARY KEY DEFAULT nextval('sppl.CUSTOMER_ID_SEQ'),
    email       TEXT,
    firstname   TEXT,
    name        TEXT
);

--
-- Products
--

CREATE TABLE IF NOT EXISTS sppl.SALES_BEER (
    beer_id         TEXT PRIMARY KEY,
    beer_name       TEXT UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS sppl.SALES_PRODUCT_ID_SEQ;
CREATE TABLE IF NOT EXISTS sppl.SALES_PRODUCT (
    product_id      INTEGER PRIMARY KEY DEFAULT nextval('sppl.SALES_PRODUCT_ID_SEQ'), 
    beer_id         TEXT,
    product_name    TEXT,
    price           NUMERIC,
    volume          NUMERIC,
    FOREIGN KEY (beer_id) REFERENCES sppl.SALES_BEER (beer_id)
);

--
-- Bottlings
--

CREATE SEQUENCE IF NOT EXISTS sppl.SALES_BOTTLINGS_ID_SEQ;
CREATE TABLE IF NOT EXISTS sppl.SALES_BOTTLINGS (
    id                  INTEGER PRIMARY KEY DEFAULT nextval('sppl.SALES_BOTTLINGS_ID_SEQ'),
    product_id          INTEGER NOT NULL,
    bottled             TIMESTAMP NOT NULL,
    best_before_date    TIMESTAMP NOT NULL,
    quantity            INTEGER NOT NULL,
    bottles             INTEGER NOT NULL,

    FOREIGN KEY (product_id) REFERENCES sppl.SALES_PRODUCT
);

--
-- Order
--

CREATE SEQUENCE IF NOT EXISTS sppl.SALES_ORDER_ID_SEQ; 
CREATE TABLE IF NOT EXISTS sppl.SALES_ORDER (
    id              INTEGER PRIMARY KEY DEFAULT nextval('sppl.SALES_ORDER_ID_SEQ'),
    employee        TEXT,
    customer        INTEGER, 
    order_date      TIMESTAMP, 
    delivery_date   TIMESTAMP,
    FOREIGN KEY (employee) REFERENCES sppl.MA_EMPLOYEES (id),
    FOREIGN KEY (customer) REFERENCES sppl.CUSTOMERS (id)
);

CREATE TABLE IF NOT EXISTS sppl.SALES_ORDER_ITEM (
    id              INTEGER,
    product         INTEGER, 
    quantity        INTEGER,
    FOREIGN KEY (id) REFERENCES sppl.SALES_ORDER (id),
    FOREIGN KEY (product) REFERENCES sppl.SALES_PRODUCT (product_id),
    PRIMARY KEY (id, product)
);