SELECT inst.amount as amount, ingr.name as ingredient, ingr.unit as unit FROM sppl.PROD_RECIPES_INSTRUCTIONS_INGREDIENT_ADDS AS inst
JOIN sppl.PROD_INGREDIENTS AS ingr ON inst.ingredient = ingr.id
WHERE inst.id = :id;
