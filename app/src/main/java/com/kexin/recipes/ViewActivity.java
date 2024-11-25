package com.kexin.recipes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.kexin.recipes.adapter.IngredientAdapter;
import com.kexin.recipes.adapter.StepAdapter;
import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;
import com.kexin.recipes.models.RecipeWithDetail;
import com.kexin.recipes.models.Step;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewActivity extends AppCompatActivity {
    EditText et_title;
    Spinner sp_category;
    ImageView iv_thumbnail;
    Boolean isFavorite;
    ImageButton bt_favorite;
    Boolean isEditMode = false;
    Button bt_edit_save;

    RecyclerView rv_ingredients;
    IngredientAdapter ingredientAdapter;
    List<Ingredient> ingredientList;
    Button bt_addIngredient;

    RecyclerView rv_steps;
    StepAdapter stepAdapter;
    List<Step> stepList;
    Button bt_addStep;

    RecipeWithDetail recipeDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        et_title = findViewById(R.id.et_title);
        sp_category = findViewById(R.id.sp_category);
        iv_thumbnail = findViewById(R.id.iv_thumbnail);
        bt_favorite = findViewById(R.id.bt_favorite);
        bt_edit_save = findViewById(R.id.bt_edit_save);

        sp_category = findViewById(R.id.sp_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(adapter);

        bt_addIngredient = findViewById(R.id.bt_addingredients);
        bt_addStep = findViewById(R.id.bt_addstep);
        bt_addIngredient.setVisibility(View.GONE);
        bt_addStep.setVisibility(View.GONE);

        rv_ingredients = findViewById(R.id.rv_ingredient);
        rv_steps = findViewById(R.id.rv_step);
        ingredientList = new ArrayList<>();
        stepList = new ArrayList<>();

        ingredientAdapter = new IngredientAdapter(ingredientList);
        stepAdapter = new StepAdapter(stepList);

        rv_ingredients.setLayoutManager(new LinearLayoutManager(this));
        rv_steps.setLayoutManager(new LinearLayoutManager(this));

        rv_ingredients.setAdapter(ingredientAdapter);
        rv_steps.setAdapter(stepAdapter);

        setupCategorySpinner();
        handleFavoriteButton();

        long recipeId = getIntent().getIntExtra("id", -1);
        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        }

        bt_edit_save.setOnClickListener(v -> {
            if (isEditMode) {
                saveRecipeUpdates();
            } else {
                enableEditMode();
            }
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(adapter);
    }

    private void handleFavoriteButton() {
        bt_favorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            bt_favorite.setImageResource(isFavorite ? R.drawable.heart_true : R.drawable.heart_false);
        });
    }

    private void loadRecipeDetails(long recipeId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "recipes").build();
            recipeDetails = db.recipeDao().getRecipeWithDetails(recipeId);

            runOnUiThread(() -> {
                if (recipeDetails != null) {
                    Recipe recipe = recipeDetails.recipe;
                    et_title.setText(recipe.getTitle());
                    sp_category.setSelection(((ArrayAdapter) sp_category.getAdapter()).getPosition(recipe.getCategory()));
                    isFavorite = recipe.getIsFavorite();
                    bt_favorite.setImageResource(isFavorite ? R.drawable.heart_true : R.drawable.heart_false);

                    ingredientList.clear();
                    ingredientList.addAll(recipeDetails.ingredients);
                    ingredientAdapter.notifyDataSetChanged();

                    stepList.clear();
                    stepList.addAll(recipeDetails.steps);
                    stepAdapter.notifyDataSetChanged();

                    if (recipe.getThumbnail() != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(recipe.getThumbnail(), 0, recipe.getThumbnail().length);
                        iv_thumbnail.setImageBitmap(bitmap);
                    }
                }
            });
        });
    }

    private void enableEditMode() {
        isEditMode = true;
        et_title.setEnabled(true);
        sp_category.setEnabled(true);
        bt_addIngredient.setVisibility(View.VISIBLE);
        bt_addStep.setVisibility(View.VISIBLE);
        bt_edit_save.setText("SAVE");
    }

    private void saveRecipeUpdates() {
        String title = et_title.getText().toString();
        String category = sp_category.getSelectedItem().toString();

        if (title.isEmpty()) {
            et_title.setError("Title cannot be empty");
            return;
        }

        byte[] thumbnail = null;
        if (iv_thumbnail.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) iv_thumbnail.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            thumbnail = stream.toByteArray();
        }

        recipeDetails.recipe.setTitle(title);
        recipeDetails.recipe.setCategory(category);
        recipeDetails.recipe.setThumbnail(thumbnail);
        recipeDetails.recipe.setIsFavorite(isFavorite);

        recipeDetails.ingredients.clear();
        recipeDetails.ingredients.addAll(ingredientAdapter.getIngredients());

        recipeDetails.steps.clear();
        recipeDetails.steps.addAll(stepAdapter.getSteps());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "recipes").build();
            db.recipeDao().updateRecipeWithDetails(recipeDetails);

            runOnUiThread(() -> {
                isEditMode = false;
                et_title.setEnabled(false);
                sp_category.setEnabled(false);
                bt_addIngredient.setVisibility(View.GONE);
                bt_addStep.setVisibility(View.GONE);
                bt_edit_save.setText("EDIT");

                Toast.makeText(ViewActivity.this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
