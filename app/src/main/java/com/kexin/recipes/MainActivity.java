package com.kexin.recipes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.kexin.recipes.adapter.RecipeAdapter;
import com.kexin.recipes.dao.RecipeDAO;
import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recipeAdapter = new RecipeAdapter(new ArrayList<>());

        recyclerView = findViewById(R.id.rcv_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recipeAdapter);
        new LoadRecipesTask().execute();
    }

    private class LoadRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "recipes").build();
            RecipeDAO recipeDAO = db.recipeDao();
            return recipeDAO.getAllRecipes();
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            if (recipes == null) {
                recipes = new ArrayList<>();
            }
            recipeAdapter = new RecipeAdapter(recipes);
            recyclerView.setAdapter(recipeAdapter);
        }
    }
    public void createClicked(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
