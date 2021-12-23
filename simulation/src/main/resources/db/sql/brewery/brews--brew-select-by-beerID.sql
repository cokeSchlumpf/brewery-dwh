SELECT
    b.id,
    b.brew_start,
    b.brew_end,
    b.original_gravity,
    b.final_gravity,
    e.id as e_id,
    e.firstname as e_firstname,
    e.name as e_name,
    e.date_of_birth as e_date_of_birth,
    e.position as e_position
FROM sppl.PROD_BREWS AS b
JOIN sppl.MA_EMPLOYEES AS e on b.brewer = e.id
WHERE b.beer=:beer;