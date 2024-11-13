package com.kexin.recipes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.kexin.recipes.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDAO {
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM Recipe WHERE id = :id")
    Recipe getById(int id);

    @Insert
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);
}
