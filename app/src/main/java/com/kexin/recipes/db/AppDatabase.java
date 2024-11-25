package com.kexin.recipes.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kexin.recipes.dao.RecipeDAO;
import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;
import com.kexin.recipes.models.Step;

@Database(entities = {Recipe.class, Ingredient.class, Step.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDao();
}
