DELETE
FROM sppl.PROD_INGREDIENT_PRODUCTS
WHERE p.producer_name = :producer_name AND p.product_name = :product_name;