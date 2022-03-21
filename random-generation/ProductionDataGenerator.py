import random
import datetime
import names
import pandas as pd
import string
from PDDataGenerator import time_stamp_generator


def generate_PROD_INGREDIENTS(count, start_id):
    name = ["water", "barely", "wheat", "oats", "rye", "bitterhop", "aromahop", "double target hop",
            "bottom fermented yeast", "top yeast", "coriander seeds", "citrus fruit"]
    ingredients = []
    for i in range(start_id, start_id + count):
        id = i
        j = random.randint(0, len(name) - 1)
        name_ingredient = name[j]
        if j == 0:
            unit = "ml"
        else:
            unit = "mg"
        ingredients.append((name_ingredient, unit))

    cols = "(name, unit)"

    return ingredients, cols


"""
Recipies
"""


def generate_PROD_RECIPIES(count, start_id, employees):
    prod_recipies = []
    for i in range(start_id, start_id + count):
        beer_id = ''.join(random.choices(string.ascii_uppercase + string.digits, k=5))
        beer_name = "Sepplpeter's beer " + beer_id
        product_owner = employees.sample()['id'].values[0]
        created = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                       datetime.datetime(2021, 1, 1, 0, 0, 0, 0), "days")
        updated = time_stamp_generator(created, datetime.datetime(2021, 10, 30, 0, 0, 0, 0), "days")
        prod_recipies.append((beer_id, beer_name, product_owner, created, updated))

    return prod_recipies


def generate_PROD_RECIPIES_INSTRUCTIONS(count, prod_recipies):
    ri = []
    for i in range(count):
        b_id = prod_recipies.sample()['beer_id'].values[0]
        beer_id = str(b_id)
        sort = random.randint(1, 10)
        ri.append((beer_id, sort))

    cols = "(beer_id, sort)"
    return ri, cols


def generate_PROD_RECIPES_INSTRUCTIONS_INGREDIENT_ADDS(count, start_id, prod_r_i, prod_ing):
    riia = []
    for i in range(start_id, start_id + count):
        id = str(prod_r_i.sample()['id'].values[0])
        ing = str(prod_ing.sample()['id'].values[0])
        amount = random.randint(1, 200)
        riia.append((id, ing, amount))
    return riia


def generate_PROD_RECIPES_INSTRUCTIONS_MASHINGS(count, start_id, prod_r_i):
    rim = []
    for i in range(start_id, start_id + count):
        id = str(prod_r_i.sample()['id'].values[0])
        start_temperature = random.randint(30, 50)
        end_temperature = random.randint(60, 100)
        duration = random.randint(5, 30)
        rim.append((id, start_temperature, end_temperature, duration))
    return rim


def generate_PROD_RECIPES_INSTRUCTIONS_MASHINGS_RESTS(count, start_id, prod_r_i):
    rimr = []
    for i in range(start_id, start_id + count):
        id = str(prod_r_i.sample()['id'].values[0])
        duration = random.randint(5, 60)
        rimr.append((id, duration))
    return rimr


def generate_PROD_RECIPES_INSTRUCTIONS_SPARGINGS(count, start_id, prod_r_i):
    ris = []
    for i in range(start_id, start_id + count):
        id = str(prod_r_i.sample()['id'].values[0])
        duration = random.randint(5, 60)
        ris.append((id, duration))
    return ris


def generate_PROD_RECIPES_INSTRUCTIONS_BOILINGS(count, start_id, prod_r_i):
    rib = []
    for i in range(start_id, start_id + count):
        id = str(prod_r_i.sample()['id'].values[0])
        duration = random.randint(5, 60)
        rib.append((id, duration))
    return rib


def generate_PROD_INGREDIENT_PRODUCTS(count, last_id, prod_ingredients):
    producer_names = []
    for i in range(15):
        producer_names.append(names.get_last_name())

    ing_products = []
    for i in range(last_id, last_id + count):
        prod_ingredient = prod_ingredients.sample()
        ing_id = prod_ingredient['id'].values[0]
        prod_pro_id = str(random.randint(1, 50))
        prod_name = producer_names[random.randint(0, 14)]
        product_name = prod_name + "'s" + prod_ingredient["name"].values[0]
        ing_products.append((str(ing_id), prod_pro_id, prod_name, product_name))

    cols = "(ingredient_id, producer_product_id, producer_name, product_name)"

    return ing_products, cols


""""
actual brew events
"""


def generate_PROD_BREWS(count, start_id, employees, prod_recipes):
    brews = []
    for i in range(start_id, start_id + count):
        beer = prod_recipes.sample()['beer_id'].values[0]
        brewer = employees.sample()['id'].values[0]
        brew_start = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                          datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
        brew_end = brew_start + datetime.timedelta(days=40)
        original_gravity = random.randint(1, 20)
        final_gravity = random.randint(1, 20)
        brews.append((beer, brewer, brew_start, brew_end, original_gravity, final_gravity))

        cols = "(beer, brewer, brew_start, brew_end, original_gravity, final_gravity)"
    return brews, cols


def generate_PROD_BREWS_INGREDIENT_ADDS(count, start_id, prod_brews, prod_ingredient_products):
    bia = []
    for i in range(start_id, start_id + count):
        brew_id = str(prod_brews.sample()['id'].values[0])
        moment = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                      datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
        ing_prod = str(prod_ingredient_products.sample()['id'].values[0])
        amount = random.randint(1, 200)
        bia.append((brew_id, moment, ing_prod, amount))
    return bia


def generate_PROD_BREWS_MASHINGS(count, start_id, prod_brew):
    bm = []
    for i in range(start_id, start_id + count):
        id = str(prod_brew.sample()['id'].values[0])
        start = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                     datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")

        end = time_stamp_generator(start, start + datetime.timedelta(hours=5), 'hours')
        start_temperature = random.randint(30, 50)
        end_temperature = random.randint(60, 100)

        bm.append((id, start, end, start_temperature, end_temperature))
    return bm


def generate_PROD_BREWS_MASHINGS_RESTS(count, start_id, prod_brew):
    bmr = []
    for i in range(start_id, start_id + count):
        id = str(prod_brew.sample()['id'].values[0])
        start = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                     datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
        end = time_stamp_generator(start, start + datetime.timedelta(hours=30), "hours")
        bmr.append((id, start, end))
    return bmr


def generate_PROD_BREWS_SPARGINGS(count, start_id, prod_brew):
    bs = []
    pk = []
    for i in range(start_id, start_id + count):

        while True:
            id = str(prod_brew.sample()['id'].values[0])
            start = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                         datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
            if not ((id, start) in pk):
                pk.append((id, start))
                break

        end = start + datetime.timedelta(hours=20)
        bs.append((id, start, end))
    return bs


def generate_PROD_BREW_BOILINGS(count, start_id, prod_brew):
    b = []
    pk = []
    for i in range(start_id, start_id + count):

        while True:
            id = str(prod_brew.sample()['id'].values[0])
            start = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0),
                                         datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
            if not ((id, start) in pk):
                pk.append((id, start))
                break

        end = start + datetime.timedelta(hours=1)
        b.append((id, start, end))
    return b


def generate_PROD_BREWS_HOP_ADDINGS(count, start_id, prod_brew):
    b = []
    pk = []
    for i in range(start_id, start_id + count):

        while True:
            id = str(prod_brew.sample()['id'].values[0])
            moment = time_stamp_generator(datetime.datetime(2015, 1, 1, 15, 0, 0, 0),
                                          datetime.datetime(2021, 6, 30, 15, 0, 0, 0), "days")
            if not ((id, moment) in pk):
                pk.append((id, moment))
                break

        amount = random.randint(1, 30)
        b.append((id, moment, amount))
    return b


def generate_PROD_BREWS_YEAST_ADDINGS(count, start_id, prod_brew):
    b = []
    pk = []
    for i in range(start_id, start_id + count):

        while True:
            id = random.randint(1, prod_brew.shape[0])
            moment = time_stamp_generator(datetime.datetime(2015, 1, 1, 15, 0, 0, 0),
                                          datetime.datetime(2021, 6, 30, 15, 0, 0, 0), "days")
            if not ((id, moment) in pk):
                pk.append((id, moment))
                break

        amount = random.randint(1, 30)
        b.append((id, moment, amount))
    return b



