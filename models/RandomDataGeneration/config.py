# This file was adapted from http://www.postgresqltutorial.com/postgresql-python/connect/

# The following config() function reads in the database.ini file and returns the connection parameters as a dict.
from configparser import ConfigParser
import psycopg2

def config(filename='database.ini', section='postgresql'):
    """
    :param filename: config.py file
    :param section: section in config.py file
    :return: connection parameters
    """
    # create a parser
    parser = ConfigParser()
    # read config file
    parser.read(filename)

    # get section, default to postgresql
    db = {}
    if parser.has_section(section):
        params = parser.items(section)
        for param in params:
            db[param[0]] = param[1]
    else:
        raise Exception('Section {0} not found in the {1} file'.format(section, filename))

    return db
