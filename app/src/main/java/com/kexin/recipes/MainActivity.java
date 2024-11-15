package com.kexin.recipes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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

        SwitchCompat switchFavorites = findViewById(R.id.switch_favorites);
        SearchView searchView = findViewById(R.id.sv_title);

        searchRecipes(searchView);
        filterFavorites(switchFavorites);

        new LoadRecipesTask().execute();
    }

    private void searchRecipes(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRecipes(newText);
                return true;
            }
        });
    }

    private void searchRecipes(String query) {
        new SearchRecipesTask().execute(query);
    }

    private void filterFavorites(@NonNull SwitchCompat switchFavorites) {
        switchFavorites.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                new LoadFavoriteRecipesTask().execute();
            } else {
                new LoadRecipesTask().execute();
            }
        });
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

    private class LoadFavoriteRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "recipes").build();
            RecipeDAO recipeDAO = db.recipeDao();
            return recipeDAO.getByFavorite();
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

    private class SearchRecipesTask extends AsyncTask<String, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(String... strings) {
            String query = strings[0];
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "recipes").build();
            RecipeDAO recipeDAO = db.recipeDao();
            return recipeDAO.searchRecipes(query);
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            recipeAdapter = new RecipeAdapter(recipes);
            recyclerView.setAdapter(recipeAdapter);
        }
    }

    public void createClicked(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
//comment
