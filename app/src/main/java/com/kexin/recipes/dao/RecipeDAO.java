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
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM Recipe WHERE id = :id")
    Recipe getById(int id);

    @Query("SELECT * FROM Recipe WHERE title LIKE :title")
    List<Recipe> getByTitle(String title);

    @Query("SELECT DISTINCT Recipe.* FROM Recipe " +
            "LEFT JOIN Ingredient ON Recipe.id = Ingredient.recipe_id " +
            "WHERE Recipe.title LIKE '%' || :query || '%' OR Ingredient.name LIKE '%' || :query || '%'")
    List<Recipe> searchRecipes(String query);

    @Query("SELECT * FROM Recipe WHERE is_favorite = 1")
    List<Recipe> getByFavorite();

    @Insert
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

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

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :recipeId")
    RecipeWithDetail getRecipeWithDetails(long recipeId);
}
