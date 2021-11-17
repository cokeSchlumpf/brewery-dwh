# Brewery DWH - Random Data Generation

## Get Started

Create a new Conda environment from the provided yml file

### Poetry Setup

Project dependencies and configurations are managed with [Poetry](https://python-poetry.org). Follow the steps below to get started.

### Poetry setup

Ensure poetry is setup properly installed on your machine.

### Install dependencies

```bash
$ conda create -p ./env python=3.8
$ conda activate ./env
$ poetry install -vvv
```

Use `conda activate ./env` when the conda environment has been created already.

## INI File 

Create a file named `database.ini`. The content of the INI file is a set of variables that establish the database connection.

```bash
[postgresql]
host = ... 
database = ...
user = ...
password = ...
```

## Generate Random Data

For generating and inserting data into the database, the jupyter notebook `DummyDataGenerator.ipynb can be executed. Each cell has to be executed in the given order. If necessary, the number of rows to write can be adjusted by the variable `num_rows` in cell 2. 