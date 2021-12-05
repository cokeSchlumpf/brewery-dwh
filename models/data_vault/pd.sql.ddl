-- *************** SqlDBM: PostgreSQL ****************;
-- ***************************************************;


-- ************************************** brewery_data_vault.h_pd_ingredients

CREATE TABLE brewery_data_vault.h_pd_ingredients
(
 hub_pd_ingredients_pk text NOT NULL,
 record_source         text NOT NULL,
 load_date             timestamp NOT NULL,
 CONSTRAINT PK_284 PRIMARY KEY ( hub_pd_ingredients_pk )
);








-- ************************************** brewery_data_vault.h_pd_experiments

CREATE TABLE brewery_data_vault.h_pd_experiments
(
 hub_pd_experiments_pk    text NOT NULL,
 hub_pd_experiments_id_bk integer NOT NULL,
 record_source            text NOT NULL,
 load_date                timestamp NOT NULL,
 CONSTRAINT PK_284 PRIMARY KEY ( hub_pd_experiments_pk )
);








-- ************************************** brewery_data_vault.s_pd_ingredients

CREATE TABLE brewery_data_vault.s_pd_ingredients
(
 hub_pd_ingredients_pk text NOT NULL,
 load_date             timestamp NOT NULL,
 name                  text NOT NULL,
 description           text NULL,
 CONSTRAINT PK_308 PRIMARY KEY ( hub_pd_ingredients_pk, load_date ),
 CONSTRAINT FK_305 FOREIGN KEY ( hub_pd_ingredients_pk ) REFERENCES brewery_data_vault.h_pd_ingredients ( hub_pd_ingredients_pk )
);

CREATE INDEX FK_307 ON brewery_data_vault.s_pd_ingredients
(
 hub_pd_ingredients_pk
);








-- ************************************** brewery_data_vault.s_pd_experiments

CREATE TABLE brewery_data_vault.s_pd_experiments
(
 brewer                  text NOT NULL,
 description             text NULL,
 exp_start               timestamp NOT NULL,
 exp_end                 timestamp NULL,
 mashing_start           timestamp NULL,
 mashing_rest_1          timestamp NULL,
 mashing_rest_1_duration integer NULL,
 mashing_rest_2          timestamp NULL,
 mashing_rest_2_duration integer NULL,
 mashing_rest_3          timestamp NULL,
 mashing_rest_3_duration integer NULL,
 mashing_rest_4          timestamp NULL,
 mashing_rest_4_duration integer NULL,
 mashing_end             timestamp NULL,
 sparging_start          timestamp NULL,
 sparging_end            timestamp NULL,
 boiling_start           timestamp NULL,
 hop_1_adding            timestamp NULL,
 hop_2_adding            timestamp NULL,
 hop_3_adding            timestamp NULL,
 boiling_end             timestamp NULL,
 yeast_adding            timestamp NULL,
 original_gravity        integer NULL,
 final_gravity           integer NULL,
 record_source           text NOT NULL,
 load_date               timestamp NOT NULL,
 hub_pd_experiments_pk   text NOT NULL,
 CONSTRAINT PK_287 PRIMARY KEY ( load_date, hub_pd_experiments_pk ),
 CONSTRAINT FK_294 FOREIGN KEY ( hub_pd_experiments_pk ) REFERENCES brewery_data_vault.h_pd_experiments ( hub_pd_experiments_pk )
);

CREATE INDEX FK_296 ON brewery_data_vault.s_pd_experiments
(
 hub_pd_experiments_pk
);








-- ************************************** brewery_data_vault.l_pd_ingredients

CREATE TABLE brewery_data_vault.l_pd_ingredients
(
 load_date             timestamp NOT NULL,
 hub_pd_experiments_pk text NOT NULL,
 record_source         text NOT NULL,
 hub_pd_ingredients_pk text NOT NULL,
 link_ingredients_pk   text NOT NULL,
 CONSTRAINT PK_312 PRIMARY KEY ( link_ingredients_pk ),
 CONSTRAINT FK_320 FOREIGN KEY ( hub_pd_experiments_pk ) REFERENCES brewery_data_vault.h_pd_experiments ( hub_pd_experiments_pk ),
 CONSTRAINT FK_323 FOREIGN KEY ( hub_pd_ingredients_pk ) REFERENCES brewery_data_vault.h_pd_ingredients ( hub_pd_ingredients_pk )
);

CREATE INDEX FK_322 ON brewery_data_vault.l_pd_ingredients
(
 hub_pd_experiments_pk
);

CREATE INDEX FK_325 ON brewery_data_vault.l_pd_ingredients
(
 hub_pd_ingredients_pk
);







