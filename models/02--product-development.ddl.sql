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
-- Experiment Tracking
--
CREATE SEQUENCE sppl.PD_EXPERIMENTS_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.PD_EXPERIMENTS (
    id                      INTEGER PRIMARY KEY DEFAULT nextval('sppl.PD_EXPERIMENTS_ID_SEQ'),
    brewer                  TEXT NOT NULL,
    description             TEXT,
    exp_start               TIMESTAMP NOT NULL,
    exp_end                 TIMESTAMP,
    mashing_start           TIMESTAMP,
    mashing_rest_1          TIMESTAMP,
    mashing_rest_1_duration INTEGER,
    mashing_rest_2          TIMESTAMP,
    mashing_rest_2_duration INTEGER,
    mashing_rest_3          TIMESTAMP,
    mashing_rest_3_duration INTEGER,
    mashing_rest_4          TIMESTAMP,
    mashing_rest_4_duration INTEGER,
    mashing_end             TIMESTAMP,
    sparging_start          TIMESTAMP,
    sparging_end            TIMESTAMP,
    boiling_start           TIMESTAMP,
    hop_1_adding            TIMESTAMP,
    hop_2_adding            TIMESTAMP,
    hop_3_adding            TIMESTAMP,
    boiling_end             TIMESTAMP,
    yeast_adding            TIMESTAMP,
    original_gravity        INTEGER,
    final_gravity           INTEGER
);

--
-- Ingredients created noted during experiments
--
CREATE SEQUENCE sppl.PD_INGRIDIENTS_ID_SEQ;

CREATE TABLE IF NOT EXISTS sppl.PD_INGREDIENTS (
    id              INTEGER PRIMARY KEY DEFAULT nextval('sppl.PD_EXPERIMENTS_ID_SEQ'),
    experiment_id   INTEGER,
    name            TEXT NOT NULL,
    description     TEXT,
    amount          INTEGER NOT NULL,
    unit            VARCHAR(10) NOT NULL,

    FOREIGN KEY (experiment_id) REFERENCES sppl.PD_EXPERIMENTS (id)
);

--
-- Internal Ratings
--
CREATE TABLE IF NOT EXISTS sppl.PD_INTERNAL_RATINGS (
    experiment_id   INTEGER,
    rated_by        TEXT,
    rating          INTEGER,

    FOREIGN KEY (experiment_id) REFERENCES sppl.PD_EXPERIMENTS (id),
    PRIMARY KEY (experiment_id, rated_by));

--
-- External Rating Events
--
CREATE TABLE IF NOT EXISTS sppl.PD_EXTERNAL_RATING_EVENTS (
    year                    INTEGER NOT NULL,
    month                   INTEGER NOT NULL,
    candidate_1             INTEGER NOT NULL,
    candidate_1_remaining   INTEGER,
    candidate_1_empty       TIMESTAMP,
    candidate_2             INTEGER NOT NULL,
    candidate_2_remaining   INTEGER,
    candidate_2_empty       TIMESTAMP,
    candidate_3             INTEGER NOT NULL,
    candidate_3_remaining   INTEGER,
    candidate_3_empty       TIMESTAMP,
    candidate_4             INTEGER NOT NULL,
    candidate_4_remaining   INTEGER,
    candidate_4_empty       TIMESTAMP,

    FOREIGN KEY (candidate_1) REFERENCES sppl.PD_EXPERIMENTS (id),
    FOREIGN KEY (candidate_2) REFERENCES sppl.PD_EXPERIMENTS (id),
    FOREIGN KEY (candidate_3) REFERENCES sppl.PD_EXPERIMENTS (id),
    FOREIGN KEY (candidate_4) REFERENCES sppl.PD_EXPERIMENTS (id),
    PRIMARY KEY (year, month)
)