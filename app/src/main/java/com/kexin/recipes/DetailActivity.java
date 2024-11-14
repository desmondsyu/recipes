package com.kexin.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.kexin.recipes.adapter.IngredientAdapter;
import com.kexin.recipes.adapter.StepAdapter;
import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;
import com.kexin.recipes.models.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    EditText et_title;
    Spinner sp_category;
    ImageView iv_thumbnail;
    Boolean isFavorite;

    RecyclerView rv_ingredients;
    IngredientAdapter ingredientAdapter;
    List<Ingredient> ingredientList;

    RecyclerView rv_steps;
    StepAdapter stepAdapter;
    List<Step> stepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // handle main data
        et_title = findViewById(R.id.et_title);
        sp_category = findViewById(R.id.sp_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(adapter);

        iv_thumbnail = findViewById(R.id.iv_thumbnail);

        isFavorite = false;

        // handle ingredients
        ingredientList = new ArrayList<>();
        rv_ingredients = findViewById(R.id.rv_ingredient);
        rv_ingredients.setLayoutManager(new LinearLayoutManager(this));

        ingredientAdapter = new IngredientAdapter(ingredientList);
        rv_ingredients.setAdapter(ingredientAdapter);

        // handle steps
        stepList = new ArrayList<>();
        rv_steps = findViewById(R.id.rv_step);
        rv_steps.setLayoutManager(new LinearLayoutManager(this));

        stepAdapter = new StepAdapter(stepList);
        rv_steps.setAdapter(stepAdapter);

    }

    public void saveClicked(View view) {
        String title = et_title.getText().toString();
        String category = sp_category.getSelectedItem().toString();

        if (title.isEmpty()) {
            et_title.setError("Title cannot be empty");
            return;
        }

        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setCategory(category);
        recipe.setThumbnail(null);
        recipe.setIsFavorite(isFavorite);


        List<Ingredient> ingredients = ingredientAdapter.getIngredients();
        List<Step> steps = stepAdapter.getSteps();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "recipes").build();

                db.recipeDao().insertRecipeWithIngredients(recipe, ingredients, steps);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "Recipe saved successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    public void addNewIngredient(View view) {
        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity("");

        ingredientAdapter.addIngredient(ingredient);
        rv_ingredients.scrollToPosition(ingredientList.size() - 1);
    }

    public void removeIngredient(View view) {
        View parent = (View) view.getParent();
        int position = rv_ingredients.getChildAdapterPosition(parent);
        if (position != RecyclerView.NO_POSITION) {
            ingredientAdapter.removeIngredient(position);
        }
    }

    public void addNewStep(View view) {
        Step step = new Step();
        stepAdapter.addStep(step);
        rv_steps.scrollToPosition(stepList.size() - 1);
    }

    public void removeStep(View view) {
        View parent = (View) view.getParent().getParent();
        int position = rv_steps.getChildAdapterPosition(parent);
        if (position != RecyclerView.NO_POSITION) {
            stepAdapter.removeStep(position);
        }
    }
}
