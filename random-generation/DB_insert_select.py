import psycopg2
from config import config
from PDDataGenerator import time_stamp_generator
import pandas as pd
import numpy
import random
import datetime
from psycopg2.extensions import register_adapter, AsIs


def addapt_numpy_float64(numpy_float64):
    return AsIs(numpy_float64)


def addapt_numpy_int64(numpy_int64):
    return AsIs(numpy_int64)


register_adapter(numpy.float64, addapt_numpy_float64)
register_adapter(numpy.int64, addapt_numpy_int64)


def insert_into(table, column_count, data, cursor, connect, col="", change_existing=True):
    if change_existing:
        update_existing_values(table, 0.05, cursor, connect)

    if len(col) > 0:
        sql = "INSERT INTO " + table + " " + col + " VALUES("
    else:
        sql = "INSERT INTO " + table + " VALUES("

    for i in range(column_count):
        if i < column_count - 1:
            sql += "%s, "
        else:
            sql += "%s)"

    cursor.executemany(sql, data)
    connect.commit()


def update_existing_values(tablename, fraction, cur, conn):
    data = pd.read_sql_query("SELECT * FROM " + tablename, conn)
    sql_update = "UPDATE " + tablename + " SET "
    to_update = data.sample(frac=fraction)
    updated_vals = []

    if tablename == "sppl.MA_EMPLOYEES":
        employee_list = ["Manager", "Assistant", "Logistic Engineer", "Sales Representative", "Brewer"]
        for i, row in to_update.iterrows():
            position = employee_list[random.randint(0, len(employee_list) - 1)]
            updated_vals.append((position, row['id']))
        sql_update = sql_update + "position = %s WHERE id = %s"

    if tablename == "sppl.PD_EXPERIMENTS":
        start = datetime.datetime(2015, 1, 1, 0, 0, 0, 0)
        end = datetime.datetime(2021, 6, 1, 0, 0, 0, 0)
        mashing_rest_duration = 5

        for i, row in to_update.iterrows():
            exp_start = time_stamp_generator(start, end, "days")
            duration = random.randint(14, 56)
            exp_end = exp_start + datetime.timedelta(days=duration)

            mashing_start = time_stamp_generator(exp_start, exp_start + datetime.timedelta(days=2), "hours")
            mashing_rest_1 = time_stamp_generator(mashing_start, mashing_start + datetime.timedelta(hours=5), "hours")
            mashing_rest_1_duration = random.randint(1, mashing_rest_duration)
            mashing_rest_2 = mashing_rest_1 + datetime.timedelta(hours=mashing_rest_1_duration)
            mashing_rest_2_duration = random.randint(1, mashing_rest_duration)
            mashing_rest_3 = mashing_rest_2 + datetime.timedelta(hours=mashing_rest_2_duration)
            mashing_rest_3_duration = random.randint(1, mashing_rest_duration)
            mashing_rest_4 = mashing_rest_3 + datetime.timedelta(hours=mashing_rest_3_duration)
            mashing_rest_4_duration = random.randint(1, mashing_rest_duration)
            mashing_end = mashing_start + datetime.timedelta(
                hours=mashing_rest_1_duration + mashing_rest_2_duration + mashing_rest_3_duration + mashing_rest_4_duration)

            sparging_start = time_stamp_generator(mashing_end, mashing_end + datetime.timedelta(days=5), "hours")
            sparging_end = sparging_start + datetime.timedelta(hours=2)
            boiling_start = sparging_end + datetime.timedelta(hours=2)
            hop_1_adding = boiling_start + datetime.timedelta(hours=2)
            hop_2_adding = hop_1_adding + datetime.timedelta(hours=2)
            hop_3_adding = hop_2_adding + datetime.timedelta(hours=2)
            boiling_end = hop_3_adding + datetime.timedelta(hours=2)
            yeast_adding = hop_3_adding + datetime.timedelta(hours=2)
            original_gravity = random.randint(1, 30)
            final_gravity = random.randint(1, 30)

            updated_vals.append(
                (exp_start, exp_end, mashing_start, mashing_rest_1, mashing_rest_1_duration,
                 mashing_rest_2, mashing_rest_2_duration, mashing_rest_3, mashing_rest_3_duration, mashing_rest_4,
                 mashing_rest_4_duration, mashing_end, sparging_start, sparging_end, boiling_start, hop_1_adding,
                 hop_2_adding,
                 hop_3_adding, boiling_end, yeast_adding, original_gravity, final_gravity, row['id']))

        sql_update = sql_update + "exp_start = %s, exp_end = %s, mashing_start = %s, mashing_rest_1=%s, mashing_rest_1_duration=%s," \
                 "mashing_rest_2=%s, mashing_rest_2_duration=%s,mashing_rest_3 =%s,mashing_rest_3_duration=%s," \
                 "mashing_rest_4=%s, mashing_rest_4_duration=%s, mashing_end =%s,sparging_start=%s," \
                 "sparging_end=%s, boiling_start=%s, hop_1_adding=%s, hop_2_adding=%s " \
                 ",hop_3_adding=%s, boiling_end=%s, yeast_adding=%s, original_gravity=%s " \
                 ",final_gravity=%s WHERE id = %s"

    if tablename == "sppl.PD_INGREDIENTS":
        for i, row in to_update.iterrows():
            amount = row['amount'] + random.uniform(-0.5, 0.5) * row['amount']
            updated_vals.append((amount, row['id']))
        sql_update = sql_update + "amount = %s WHERE id = %s"

    if tablename == "sppl.PD_INTERNAL_RATINGS":
        for i, row in to_update.iterrows():
            rating = int(row['rating'] + random.uniform(-0.5, 0.5) * row['rating'])
            updated_vals.append((rating, row['experiment_id'], row['rated_by']))

        sql_update = sql_update + "rating = %s WHERE experiment_id = %s AND rated_by = %s"

    if tablename == "sppl.PD_EXTERNAL_RATING_EVENTS":
        for i, row in to_update.iterrows():
            c1r = row['candidate_1_remaining'] + random.uniform(-0.5, 0) * row['candidate_1_remaining']
            c2r = row['candidate_2_remaining'] + random.uniform(-0.5, 0) * row['candidate_2_remaining']
            c3r = row['candidate_3_remaining'] + random.uniform(-0.5, 0) * row['candidate_3_remaining']
            c4r = row['candidate_4_remaining'] + random.uniform(-0.5, 0) * row['candidate_4_remaining']

            c1e = row['candidate_1_empty'] + datetime.timedelta(days=random.uniform(-15, 15))
            c2e = row['candidate_2_empty'] + datetime.timedelta(days=random.uniform(-15, 15))
            c3e = row['candidate_3_empty'] + datetime.timedelta(days=random.uniform(-15, 15))
            c4e = row['candidate_4_empty'] + datetime.timedelta(days=random.uniform(-15, 15))
            updated_vals.append((c1r, c2r, c3r, c4r, c1e, c2e, c3e, c4e, row['year'], row['month']))

        sql_update = sql_update + "candidate_1_remaining = %s, candidate_2_remaining = %s, candidate_3_remaining = " \
                                  "%s, candidate_4_remaining = %s, candidate_1_empty = %s, candidate_2_empty = %s, " \
                                  "candidate_3_empty = %s, candidate_4_empty = %s WHERE year = %s AND month = %s "

    """
    Production 
    """

    if tablename == "sppl.PROD_RECIPES":
        for i, row in to_update.iterrows():
            updated = row['updated'] + datetime.timedelta(days=random.uniform(-15, 15))
            updated_vals.append((updated, row['beer_id']))
        sql_update = sql_update + "updated = %s WHERE beer_id = %s"

    if tablename == "sppl.PROD_RECIPES_INSTRUCTIONS_INGREDIENT_ADDS":
        for i, row in to_update.iterrows():
            updated = row['amount'] + random.uniform(-0.5, 0.5) * row['amount']
            beer_id = row['id']
            updated_vals.append((updated, beer_id))
        sql_update = sql_update + "amount = %s WHERE id = %s"

    if tablename == "sppl.PROD_RECIPES_INSTRUCTIONS_MASHINGS":
        for i, row in to_update.iterrows():
            start_temperature = row['start_temperature'] + random.uniform(-0.5, 0.5) * row['start_temperature']
            end_temperature = row['end_temperature'] + random.uniform(-0.5, 0.5) * row['end_temperature']
            duration = row['duration'] + random.uniform(-0.5, 0.5) * row['duration']
            updated_vals.append((start_temperature, end_temperature, duration, row['id']))
        sql_update = sql_update + "start_temperature = %s,end_temperature = %s, duration = %s WHERE id = %s"

    if tablename == "sppl.PROD_RECIPES_INSTRUCTIONS_MASHING_RESTS":
        for i, row in to_update.iterrows():
            duration = row['duration'] + random.uniform(-0.5, 0.5) * row['duration']
            updated_vals.append((duration, row['id']))
        sql_update = sql_update + "duration = %s WHERE id = %s"

    if tablename == "sppl.PROD_RECIPES_INSTRUCTIONS_SPARGINGS":
        for i, row in to_update.iterrows():
            duration = row['duration'] + random.uniform(-0.5, 0.5) * row['duration']
            updated_vals.append((duration, row['id']))
        sql_update = sql_update + "duration = %s WHERE id = %s"

    if tablename == "sppl.PROD_RECIPES_INSTRUCTIONS_BOILINGS":
        for i, row in to_update.iterrows():
            duration = row['duration'] + random.uniform(-0.5, 0.5) * row['duration']
            updated_vals.append((duration, row['id']))
        sql_update = sql_update + "duration = %s WHERE id = %s"

    if tablename == "sppl.PROD_BREWS":
        for i, row in to_update.iterrows():
            brew_start = row['brew_start'] + datetime.timedelta(hours=random.uniform(-1, 1))
            brew_end = row['brew_end'] + datetime.timedelta(hours=random.uniform(1, 4))
            original_gravity = row['original_gravity'] + random.randint(-5, 5)
            final_gravity = row['final_gravity'] + random.randint(-5, 5)
            updated_vals.append((brew_start, brew_end, original_gravity, final_gravity, row['id']))

        sql_update = sql_update + "brew_start = %s,brew_end = %s,original_gravity = %s,final_gravity = %s WHERE id = %s"

    if tablename == "sppl.PROD_BREWS_INGREDIENT_ADDS":
        for i, row in to_update.iterrows():
            moment = row['moment'] + datetime.timedelta(hours=random.uniform(-48, 48))
            amount = row['amount'] + random.uniform(-0.5, 0.5) * row['amount']
            updated_vals.append((moment, amount, row['brew_id'], row['ingredient_product']))

        sql_update = sql_update + "moment = %s,amount = %s WHERE brew_id = %s AND ingredient_product = %s"

    if tablename == "sppl.PROD_BREWS_MASHINGS":
        for i, row in to_update.iterrows():
            end_time = row['end_time'] + datetime.timedelta(hours=random.uniform(0, 48))
            start_temperature = row['start_temperature'] + random.uniform(-0.5, 0.5) * row['start_temperature']
            end_temperature = row['end_temperature'] + random.uniform(-0.5, 0.5) * row['end_temperature']
            updated_vals.append((end_time, start_temperature, end_temperature, row['brew_id'], row['start_time']))

        sql_update = sql_update + "end_time = %s,start_temperature = %s,end_temperature = %s  WHERE brew_id = %s AND " \
                                  "start_time = %s "

    if tablename == "sppl.PROD_BREWS_MASHING_RESTS":
        for i, row in to_update.iterrows():
            end_time = row['end_time'] + datetime.timedelta(hours=random.uniform(0, 24))
            updated_vals.append((end_time, row['brew_id'], row['start_time']))
        sql_update = sql_update + "end_time = %s WHERE brew_id = %s AND start_time = %s"

    if tablename == "sppl.PROD_BREWS_SPARGINGS":
        for i, row in to_update.iterrows():
            end_time = row['end_time'] + datetime.timedelta(hours=random.uniform(0, 24))
            updated_vals.append((end_time, row['brew_id'], row['start_time']))
        sql_update = sql_update + "end_time = %s WHERE brew_id = %s AND start_time = %s"

    if tablename == "sppl.PROD_BREWS_BOILINGS":
        for i, row in to_update.iterrows():
            end_time = row['end_time'] + datetime.timedelta(hours=random.uniform(0, 24))
            updated_vals.append((end_time, row['brew_id'], row['start_time']))
        sql_update = sql_update + "end_time = %s WHERE brew_id = %s AND start_time = %s"

    if len(updated_vals) >= 1:
        cur.executemany(sql_update, updated_vals)
        conn.commit()
