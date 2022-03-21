import random
import datetime


def time_stamp_generator(start, end, scale):
    delta = end - start
    if (scale == "days"):
        days = delta.days
        random_number_of_days = random.randrange(days)
        random_date = start + datetime.timedelta(days=random_number_of_days)
    elif (scale == "hours"):
        hours = delta.total_seconds() / 3600
        random_number_of_hours = random.randrange(int(hours))
        random_date = start + datetime.timedelta(hours=random_number_of_hours)

    return random_date


def generate_PD_EXPERIMENTS(count, start_id):
    brewers = ["Mike", "Eleanor", "Johnny"]
    description = 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. ' \
                  'Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, ' \
                  'purus lectus malesuada libero, sit amet commodo magna eros quis urna. Nunc viverra imperdiet enim. ' \
                  'Fusce est. Vivamus a tellus. Pellentesque habitant morbi tristique senectus et netus et ' \
                  'malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.'
    start = datetime.datetime(2015, 1, 1, 0, 0, 0, 0)
    end = datetime.datetime(2021, 6, 1, 0, 0, 0, 0)

    mashing_rest_duration = 5

    experiments = []
    for i in range(start_id, start_id + count):
        brewer = brewers[random.randint(0, 2)]

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

        experiments.append(
             (brewer, description, exp_start, exp_end, mashing_start, mashing_rest_1, mashing_rest_1_duration,
              mashing_rest_2, mashing_rest_2_duration, mashing_rest_3, mashing_rest_3_duration, mashing_rest_4,
              mashing_rest_4_duration, mashing_end, sparging_start, sparging_end, boiling_start, hop_1_adding,
              hop_2_adding,
              hop_3_adding, boiling_end, yeast_adding, original_gravity, final_gravity))

    cols = "(brewer, description, exp_start, exp_end, mashing_start, mashing_rest_1, mashing_rest_1_duration, " \
                 "mashing_rest_2, mashing_rest_2_duration,mashing_rest_3 ,mashing_rest_3_duration," \
                 "mashing_rest_4, mashing_rest_4_duration, mashing_end ,sparging_start," \
                 "sparging_end, boiling_start, hop_1_adding, hop_2_adding " \
                 ",hop_3_adding, boiling_end, yeast_adding, original_gravity " \
                 ",final_gravity)"

    return experiments, cols


def generate_PD_INGREDIENTS(count, experiments):
    name = ["water", "barely", "wheat", "oats", "rye", "bitterhop", "aromahop", "double target hop",
            "bottom fermented yeast", "top yeast", "coriander seeds", "citrus fruit"]

    ingredients = []

    for i in range(count):
        exp_id = experiments.sample()['id'].values[0]
        number_ingredients = random.randint(1, 5)
        for j in range(number_ingredients):
            ingredient_name = name[random.randint(0, len(name) - 1)]
            amount = random.randint(1, 100)
            ingredients.append((str(exp_id), ingredient_name, "Ingredient Description", amount, "gram"))

    cols = "(experiment_id, name, description, amount, unit)"

    return ingredients, cols


def generate_PD_INTERNAL_RATINGS(count, experiments, db):
    brewers = ["Mike", "Eleanor", "Johnny"]
    ratings = []
    exp_ids = []
    for i in range(count):
        exp_id = experiments.sample()['id'].values[0]
        if(exp_id in list(db['experiment_id']) or exp_id in exp_ids):
            continue
        exp_ids.append(exp_id)
        for rated_by in brewers:
            rating = random.randint(1, 10)
            ratings.append((str(exp_id), rated_by, rating))

    return ratings


def generate_PD_EXTERNAL_RATING_EVENTS(count,experiments, db):
    external_ratings = []

    year_month= []

    for i in range(count):

        year = random.randint(1950, 2020)
        month = random.randint(1, 12)
        if (year, month) in year_month or (year, month) in db[['year', 'month']].values:
            continue

        year_month.append((year, month))
        rating = (int(year), int(month))
        for i in range(4):
            exp = random.randint(0, experiments.shape[0]-1)
            exp_id = experiments.iloc[exp, 0]
            remaining = random.randint(1, 50)
            empty = time_stamp_generator(datetime.datetime(year, month, 1, 0, 0, 0, 0), datetime.datetime(year, month, 1, 0, 0, 0,0) + datetime.timedelta(days=30),"days")
            rating = rating + (int(exp_id), int(remaining), empty)

        external_ratings.append(rating)

    return external_ratings
