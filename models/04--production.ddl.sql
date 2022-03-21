-- ==================================================
-- Author: Michael Wellner <michael.wellner@gmail.com>
-- Create date: 2021-10-29
-- Description: Description of data model of transactional data stores of Sepplpeter's MicroBrewery
-- ==================================================

--
-- create schema
--
CREATE SCHEMA IF NOT EXISTS sppl;

--
-- Ingredients
--

CREATE SEQUENCE IF NOT EXISTS sppl.PROD_INGREDIENTS_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.PROD_INGREDIENTS (
    id          INTEGER PRIMARY KEY DEFAULT nextval('sppl.PROD_INGREDIENTS_ID_SEQ'),
    name        VARCHAR(64),
    unit        VARCHAR(6)
);

--
-- Recipes
--
CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES (
    beer_id         TEXT PRIMARY KEY,
    beer_name       TEXT UNIQUE,
    product_owner   TEXT,
    created         TIMESTAMP,
    updated         TIMESTAMP,

    FOREIGN KEY (product_owner) REFERENCES sppl.MA_EMPLOYEES (id)
);

CREATE SEQUENCE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS (
    id              INTEGER PRIMARY KEY DEFAULT nextval('sppl.PROD_RECIPES_INSTRUCTIONS_ID_SEQ'),
    beer_id         TEXT,
	sort            INTEGER,

    FOREIGN KEY (beer_id) REFERENCES sppl.PROD_RECIPES
);

CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS_INGREDIENT_ADDS (
    id              INTEGER,
    ingredient      INTEGER,
    amount          NUMERIC,

    FOREIGN KEY (id) REFERENCES sppl.PROD_RECIPES_INSTRUCTIONS (id),
    FOREIGN KEY (ingredient) REFERENCES sppl.PROD_INGREDIENTS (id)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS_MASHINGS (
    id                  INTEGER,
    start_temperature   NUMERIC,
    end_temperature     NUMERIC,
    duration            INTEGER,

    FOREIGN KEY (id) REFERENCES sppl.PROD_RECIPES_INSTRUCTIONS (id)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS_MASHING_RESTS (
    id                  INTEGER,
    duration            INTEGER,

    FOREIGN KEY (id) REFERENCES sppl.PROD_RECIPES_INSTRUCTIONS (id)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS_SPARGINGS (
    id                  INTEGER,
    duration            INTEGER,
	
    FOREIGN KEY (id) REFERENCES sppl.PROD_RECIPES_INSTRUCTIONS (id)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_RECIPES_INSTRUCTIONS_BOILINGS (
    id                  INTEGER,
    duration            INTEGER,
    
	FOREIGN KEY (id) REFERENCES sppl.PROD_RECIPES_INSTRUCTIONS (id)
);

--
-- Ingredient Products
--

CREATE SEQUENCE IF NOT EXISTS sppl.PROD_INGREDIENT_PRODUCTS_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.PROD_INGREDIENT_PRODUCTS (
    id                      INTEGER PRIMARY KEY DEFAULT nextval('sppl.PROD_INGREDIENT_PRODUCTS_ID_SEQ'),
    ingredient_id           INTEGER,
    producer_product_id     TEXT,
    producer_name           TEXT,
    product_name            TEXT,

    FOREIGN KEY (ingredient_id) REFERENCES sppl.PROD_INGREDIENTS(id)
);


--
-- Brews main table
--

CREATE SEQUENCE IF NOT EXISTS sppl.PROD_BREWS_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.PROD_BREWS (
    id                  INTEGER PRIMARY KEY DEFAULT nextval('sppl.PROD_BREWS_ID_SEQ'),
    beer                TEXT,
    brewer              TEXT NOT NULL,
    brew_start          TIMESTAMP NOT NULL,
    brew_end            TIMESTAMP,
    original_gravity    INTEGER,
    final_gravity       INTEGER,

    FOREIGN KEY (brewer) REFERENCES sppl.MA_EMPLOYEES (id),
    FOREIGN KEY (beer) REFERENCES sppl.PROD_RECIPES (beer_id)
);

--
-- Brew events
--

CREATE TABLE IF NOT EXISTS sppl.PROD_BREWS_INGREDIENT_ADDS (
    brew_id             INTEGER,
    moment              TIMESTAMP,
    ingredient_product  INTEGER,
    amount              NUMERIC,

    FOREIGN KEY (brew_id) REFERENCES sppl.PROD_BREWS (id),
    FOREIGN KEY (ingredient_product) REFERENCES sppl.PROD_INGREDIENT_PRODUCTS(id)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_BREWS_MASHINGS (
    brew_id              INTEGER,
    start_time           TIMESTAMP,
    end_time             TIMESTAMP,
    start_temperature    INTEGER,
    end_temperature      INTEGER,

    FOREIGN KEY (brew_id) REFERENCES sppl.PROD_BREWS (id),
    PRIMARY KEY (brew_id, start_time)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_BREWS_MASHING_RESTS (
    brew_id             INTEGER,
    start_time          TIMESTAMP,
    end_time            TIMESTAMP,
    FOREIGN KEY (brew_id) REFERENCES sppl.PROD_BREWS (id),
    PRIMARY KEY (brew_id, start_time)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_BREWS_SPARGINGS (
    brew_id             INTEGER,
    start_time          TIMESTAMP,
    end_time            TIMESTAMP,
    FOREIGN KEY (brew_id) REFERENCES sppl.PROD_BREWS (id),
    PRIMARY KEY (brew_id, start_time)
);

CREATE TABLE IF NOT EXISTS sppl.PROD_BREWS_BOILINGS (
    brew_id             INTEGER,
    start_time          TIMESTAMP,
    end_time            TIMESTAMP,
    FOREIGN KEY (brew_id) REFERENCES sppl.PROD_BREWS (id),
    PRIMARY KEY (brew_id, start_time)
);