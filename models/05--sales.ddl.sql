--
-- create schema
--
CREATE SCHEMA IF NOT EXISTS sppl;

--
-- Customers
--

CREATE SEQUENCE IF NOT EXISTS sppl.SALES_CUSTOMER_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.SALES_CUSTOMERS (
    id          INTEGER PRIMARY KEY DEFAULT nextval('sppl.SALES_CUSTOMER_ID_SEQ'),
    email       TEXT,
    firstname   TEXT,
    name        TEXT,
    street      TEXT,
    zip_code    TEXT,
    city        TEXT
);

--
-- Products
--

CREATE SEQUENCE IF NOT EXISTS sppl.SALES_PRODUCT_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.SALES_PRODUCT (
    product_id      INTEGER PRIMARY KEY DEFAULT nextval('sppl.SALES_PRODUCT_ID_SEQ'), 
    beer_id         TEXT,
    product_name    TEXT,
    price           NUMERIC,
    volume          NUMERIC
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
    bottles             INTEGER NOT NULL,

    FOREIGN KEY (product_id) REFERENCES sppl.SALES_PRODUCT
);


---
--- Stock
---

CREATE TABLE IF NOT EXISTS sppl.SALES_STOCK_PRODUCT (
    product_id          INTEGER PRIMARY KEY,
    bottles             INTEGER NOT NULL,
    reserved            INTEGER NOT NULL,

    FOREIGN KEY (product_id) REFERENCES sppl.SALES_PRODUCT
);

--
-- Order
--

CREATE SEQUENCE IF NOT EXISTS sppl.SALES_ORDER_ID_SEQ; 

CREATE TABLE IF NOT EXISTS sppl.SALES_ORDER (
    id              INTEGER PRIMARY KEY DEFAULT nextval('sppl.SALES_ORDER_ID_SEQ'),
    customer        INTEGER, 
    order_date      TIMESTAMP,
    delivery_date   TIMESTAMP,

    FOREIGN KEY (customer) REFERENCES sppl.SALES_CUSTOMERS (id)
);

CREATE TABLE IF NOT EXISTS sppl.SALES_ORDER_ITEM (
    id              INTEGER,
    product         INTEGER, 
    quantity        INTEGER,

    FOREIGN KEY (id) REFERENCES sppl.SALES_ORDER (id),
    FOREIGN KEY (product) REFERENCES sppl.SALES_PRODUCT (product_id),
    PRIMARY KEY (id, product)
);