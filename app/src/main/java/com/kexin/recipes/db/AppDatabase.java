package com.kexin.recipes.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kexin.recipes.dao.RecipeDAO;
import com.kexin.recipes.models.Recipe;

@Database(entities = {Recipe.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDao();
}
