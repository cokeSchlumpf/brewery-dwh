package systems.brewery;

import systems.brewery.values.Ingredient;

import java.util.Optional;

/**
 * Interface to manage ingredients.
 */
public interface Ingredients {

    /*
     * create
     */
    void insertIngredient(Ingredient ingredient);

    /*
     * read
     */
    Optional<Ingredient> getIngredientByName(String name);

    int getIngredientIdByName(String name);

    /*
     * update
     */
    void insertOrUpdateIngredient(Ingredient ingredient);

    /*
     * delete
     */
    void removeIngredient(String ingredient);

    void clear();

}
