SELECT
    p.id,
    p.producer_product_id,
    p.producer_name,
    p.product_name,
    i.name as ingredient_name,
    i.unit as ingredient_unit
FROM sppl.PROD_INGREDIENT_PRODUCTS AS p
JOIN sppl.PROD_INGREDIENTS AS i ON p.ingredient_id = i.id
WHERE i.name = :ingredient_name;