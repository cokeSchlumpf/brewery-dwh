SELECT
    r.beer_id as beer_id,
    r.beer_name as beer_name,
    r.created as created,
    r.updated as updated,
    e.id as e_id,
    e.firstname as e_firstname,
    e.name as e_name,
    e.date_of_birth as e_date_of_birth,
    e.position as e_position
FROM sppl.PROD_RECIPES AS r
JOIN sppl.MA_EMPLOYEES AS e on r.product_owner = e.id
WHERE beer_id = :beer_id;