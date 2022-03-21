import names
import random
import datetime
from PDDataGenerator import time_stamp_generator
import string


def generate_SALES_CUSTOMERS(count):
    email_list = ["@gmail.com", "@outlook.com", "@yahoo.com"]
    street_list = ["Street", "Avenue", "Road", "Drive", "Lane"]
    city_list = ["San Francisco", "New York City", "Zurich", "Basel", "Berlin", "Paris"]
    customers = []
    for i in range(count):
        first_name = names.get_first_name()
        name = names.get_last_name()
        email = first_name + "." + name + email_list[random.randint(0, len(email_list) - 1)]
        street = names.get_last_name() + street_list[random.randint(0, len(street_list) - 1)]
        zip = "31415"
        city = city_list[random.randint(0, len(city_list) - 1)]
        customers.append((email, first_name, name, street, zip, city))

    col = "(email, firstname, name, street, zip_code, city)"
    return customers, col


def generate_SALES_PRODUCT(count, prod_recipes):

    product_name_list = ["Standard Edition", "Holiday Edition", "Extra Large", "Alcoholfree", "Special Edition"]
    volume_list = [0.25, 0.33, 0.5,0.75]
    products = []

    for i in range(count):
        beer_id = prod_recipes.sample()['beer_id'].values[0]
        product_name = "Sepplpeter's beer " + beer_id + " " + product_name_list[random.randint(0, len(product_name_list) - 1)]
        price = round(random.uniform(0.8,2.0),2)
        volume = volume_list[random.randint(0, len(volume_list) - 1)]
        products.append((beer_id, product_name, price, volume))

    cols = "(beer_id, product_name, price, volume)"
    return products, cols


"""
Bottlings
"""


def generate_SALES_BOTTLINGS(count, products, db):
    bottlings = []
    product_ids = []
    for i in range(count):
        product_id = int(products.sample()['product_id'].values[0])

        if product_id in product_ids or product_id in db[['id']].values:
            continue
        product_ids.append(product_id)
        bottled = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0), datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
        best_before = bottled + datetime.timedelta(days= random.randint(150,200))
        bottles = random.randint(50, 200)
        bottlings.append((product_id, bottled, best_before, bottles))

    cols = "(product_id, bottled, best_before_date, bottles)"
    return bottlings, cols

"""
Stock
"""


def generate_SALES_STOCK_PRODUCT(count, products, db):
    stock = []
    product_ids = []
    for i in range(count):
        product_id = products.sample()['product_id'].values[0]
        if product_id in product_ids or product_id in db[['product_id']].values:
            continue

        product_ids.append(product_id)
        bottles = random.randint(300,1000)
        reserved = random.randint(20,600)
        stock.append((int(product_id), bottles, reserved))
    return stock

"""
Order
"""


def generate_SALES_ORDER(count, customers):
    orders = []
    for i in range(count):
        customer = int(customers.sample()['id'].values[0])
        order_date = time_stamp_generator(datetime.datetime(2015, 1, 1, 0, 0, 0, 0), datetime.datetime(2021, 6, 30, 0, 0, 0, 0), "days")
        delivery_date = order_date + datetime.timedelta(days= random.randint(1,7))
        orders.append((customer,order_date, delivery_date))

    cols = "(customer, order_date, delivery_date)"
    return orders, cols


def generate_SALES_ORDER_ITEM(count, orders, products, order_items_db):

    order_items = []

    order_products = []
    for i in range(count):
        order = int(orders.sample()['id'].values[0])
        product = int(products.sample()['product_id'].values[0])
        if (order, product) in order_products or (order, product) in order_items_db[['id', 'product']].values:
            continue

        order_products.append((order,product))
        quantity = random.randint(2,24)
        order_items.append((order, product, quantity))
    if(len(order_items) == 0): print("No unique further (order,product) combinations left.")
    return order_items