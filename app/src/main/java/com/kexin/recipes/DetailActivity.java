package com.kexin.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kexin.recipes.adapter.IngredientAdapter;
import com.kexin.recipes.adapter.StepAdapter;
import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;
import com.kexin.recipes.models.Step;
import com.kexin.recipes.singleton.AppDatabaseInstance;
import com.kexin.recipes.utils.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    EditText et_title;
    Spinner sp_category;
    ImageView iv_thumbnail;
    Boolean isFavorite;
    ImageButton bt_favorite;

    RecyclerView rv_ingredients;
    IngredientAdapter ingredientAdapter;
    List<Ingredient> ingredientList;

    RecyclerView rv_steps;
    StepAdapter stepAdapter;
    List<Step> stepList;

    HashMap<String, Integer> categoryImageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // handle main data
        et_title = findViewById(R.id.et_title);
        sp_category = findViewById(R.id.sp_category);
        iv_thumbnail = findViewById(R.id.iv_thumbnail);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(adapter);

        ThumbnailUtils.setCategoryThumbnail(iv_thumbnail, null);
        sp_category.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parentView, View view, int position, long id) {
                String selectedCategory = (String) parentView.getItemAtPosition(position);
                ThumbnailUtils.setCategoryThumbnail(iv_thumbnail, selectedCategory);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parentView) {
                ThumbnailUtils.setCategoryThumbnail(iv_thumbnail, null);
            }
        });

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

        //favorite button
        bt_favorite = findViewById(R.id.bt_favorite);

        bt_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    bt_favorite.setImageResource(R.drawable.heart_false);
                    isFavorite = false;
                } else {
                    bt_favorite.setImageResource(R.drawable.heart_true);
                    isFavorite = true;
                }
            }
        });
    }

    public void saveClicked(View view) {
        String title = et_title.getText().toString();
        String category = sp_category.getSelectedItem().toString();

        if (title.isEmpty()) {
            et_title.setError("Title cannot be empty");
            return;
        }

        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateRecipe()) {
            return;
        }

        byte[] thumbnail = null;
        if (iv_thumbnail.getDrawable() != null &&
                iv_thumbnail.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.default_thumbnail).getConstantState()) {

            BitmapDrawable drawable = (BitmapDrawable) iv_thumbnail.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // Use PNG if preferred
            thumbnail = stream.toByteArray();
        }

        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setCategory(category);
        recipe.setThumbnail(thumbnail);
        recipe.setIsFavorite(isFavorite);

        List<Ingredient> ingredients = ingredientAdapter.getIngredients();
        List<Step> steps = stepAdapter.getSteps();

        //Save to favorites if clicked:
        isFavorite = bt_favorite.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.heart_true).getConstantState());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabaseInstance.getInstance(getApplicationContext());

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

    private boolean validateRecipe() {
        if (et_title.getText().toString().trim().isEmpty()) {
            et_title.setError("Title cannot be empty");
            return false;
        }
        if (ingredientAdapter.getIngredients().isEmpty()) {
            Toast.makeText(this, "Add at least one ingredient", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (stepAdapter.getSteps().isEmpty()) {
            Toast.makeText(this, "Add at least one step", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
