SELECT
    p.product_id, p.beer_id, p.product_name, p.price, p.volume,
    s.bottles, s.reserved
FROM sppl.SALES_STOCK_PRODUCT AS s
JOIN sppl.SALES_PRODUCT AS p ON s.product_id = p.product_id
WHERE s.product_id = :product_id;