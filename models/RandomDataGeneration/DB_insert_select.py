import psycopg2
from config import config
import pandas as pd


def insert_into(table, column_count, data, cursor, connect):
    sql = "INSERT INTO "+ table + " VALUES("

    for i in range(column_count):
        if i < column_count - 1:
            sql += "%s, "
        else:
            sql += "%s)"
    cursor.executemany(sql, data)
    connect.commit()
