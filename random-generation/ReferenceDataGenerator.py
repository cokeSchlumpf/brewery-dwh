import names
import random
import datetime
import string

def generate_MA_EMPLOYEES(count, start_id):
    employee_list = ["Manager", "Assistant", "Logistic Engineer", "Sales Representative", "Brewer"]
    start_dob = datetime.date(1960, 1, 1)
    end_dob = datetime.date(2000, 1, 1)

    employees = []
    for i in range(start_id, start_id + count):
        id = ''.join(random.choices(string.ascii_uppercase + string.digits, k=10))
        first_name = names.get_first_name()
        last_name = names.get_last_name()

        time_delta = end_dob - start_dob
        days = time_delta.days
        random_number_of_days = random.randrange(days)
        dob = start_dob + datetime.timedelta(days=random_number_of_days)

        position = employee_list[random.randint(0, len(employee_list) - 1)]

        employees.append((id, first_name, last_name, dob, position))

    return employees
