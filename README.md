# Brewery DWH
A repository which includes a scenario, models and real-world simulation for Data Warehouse education. 

The Datenbräu MicroBrewery is a fictional enterprise. A detailed description of the brewery's business processes can be found [here](https://github.com/cokeSchlumpf/brewery-dwh/blob/main/01--process-definitions.md). 
Within Datenbräu MicroBrewery there are various data sources. The schema is defined in [models](https://github.com/cokeSchlumpf/brewery-dwh/tree/main/models).
This reflects the situation and the resulting challenges that many real world enterprises face today. This repository provides the chance for users to experiment with solutions for integrating data from different
source systems to a data warehouse for e.g. analytical purposes without using any sensitive data from real world enterprises. 

## Deployment
To get started create a PostgreSQL database with the corresponding schema from the operational data systems of the brewery. 
For a straightforward deployment follow this [readme](https://github.com/cokeSchlumpf/brewery-dwh/tree/main/deployment). After this step you can start filling
the database with 

## Real-World Simulation
The daily business of the brewery can be simulated with the provided simulation. Note that this is still in progress and not all tables from the defined schema can be filled with this simulation.

## Dummy Data Generation
Because the simulation itself is still in progress, we implemented a dummy generator to generate large amounts of data. While the generated data is not
very insightful itself and therefore should not be used for analytical purposes, it can still be used for implementing and testing purposes. 
Detailed instructions on how to generate the data can be found [here](https://github.com/cokeSchlumpf/brewery-dwh/tree/main/random-generation).
