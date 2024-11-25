package com.kexin.recipes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;
import com.kexin.recipes.models.RecipeWithDetail;
import com.kexin.recipes.models.Step;

import java.util.List;

@Dao
public interface RecipeDAO {
    @Query("SELECT * FROM Recipe ORDER BY id DESC")
    List<Recipe> getAllRecipes();

    @Query("SELECT DISTINCT Recipe.* FROM Recipe " +
            "LEFT JOIN Ingredient ON Recipe.id = Ingredient.recipe_id " +
            "WHERE Recipe.title LIKE '%' || :query || '%' OR Ingredient.name LIKE '%' || :query || '%'" +
            "ORDER BY Recipe.id DESC")
    List<Recipe> searchRecipes(String query);

    @Query("SELECT * FROM Recipe WHERE is_favorite = 1 ORDER BY id DESC")
    List<Recipe> getByFavorite();

//    @Delete
//    void delete(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRecipe(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredients(List<Ingredient> ingredients);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSteps(List<Step> steps);

    @Transaction
    default void insertRecipeWithIngredients(Recipe recipe, List<Ingredient> ingredients, List<Step> steps) {
        long recipeId = insertRecipe(recipe);

        for (Ingredient ingredient : ingredients) {
            ingredient.setRecipeId((int) recipeId);
        }
        insertIngredients(ingredients);

        for (Step step : steps) {
            step.setRecipeId((int) recipeId);
        }
        insertSteps(steps);
    }

    @Update
    void update(Recipe recipe);

    @Update
    void updateIngredient(Ingredient ingredient);

    @Update
    void updateStep(Step step);

    @Transaction
    default void updateRecipeWithDetails(RecipeWithDetail recipeDetails) {
        update(recipeDetails.recipe);

        for (Ingredient ingredient : recipeDetails.ingredients) {
            updateIngredient(ingredient);
        }

        for (Step step : recipeDetails.steps) {
            updateStep(step);
        }
    }

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :recipeId")
    RecipeWithDetail getRecipeWithDetails(long recipeId);

    @Query("DELETE FROM Ingredient WHERE recipe_id = :recipeId")
    void deleteIngredientsByRecipeId(long recipeId);

    @Query("DELETE FROM Step WHERE recipe_id = :recipeId")
    void deleteStepsByRecipeId(long recipeId);

    @Transaction
    default void updateRecipeAndReplaceDetails(Recipe recipe, List<Ingredient> ingredients, List<Step> steps) {
        update(recipe);

        deleteIngredientsByRecipeId(recipe.getId());
        deleteStepsByRecipeId(recipe.getId());

        for (Ingredient ingredient : ingredients) {
            ingredient.setRecipeId((int) recipe.getId());
        }
        insertIngredients(ingredients);

        for (Step step : steps) {
            step.setRecipeId((int) recipe.getId());
        }
        insertSteps(steps);
    }
}
