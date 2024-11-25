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

import com.kexin.recipes.adapter.IngredientAdapter;
import com.kexin.recipes.adapter.StepAdapter;
import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;
import com.kexin.recipes.models.RecipeWithDetail;
import com.kexin.recipes.models.Step;
import com.kexin.recipes.singleton.AppDatabaseInstance;
import com.kexin.recipes.utils.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewActivity extends AppCompatActivity {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

        initUI();
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

    private void initUI() {
        et_title = findViewById(R.id.et_title);
        et_title.setEnabled(false);

        iv_thumbnail = findViewById(R.id.iv_thumbnail);

        sp_category = findViewById(R.id.sp_category);
        sp_category.setEnabled(false);
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

        bt_favorite = findViewById(R.id.bt_favorite);
        bt_edit_save = findViewById(R.id.bt_edit_save);

        bt_addIngredient = findViewById(R.id.bt_addingredients);
        bt_addIngredient.setVisibility(View.GONE);
        rv_ingredients = findViewById(R.id.rv_ingredient);
        ingredientList = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(ingredientList);
        ingredientAdapter.setEditMode(false);
        rv_ingredients.setLayoutManager(new LinearLayoutManager(this));
        rv_ingredients.setAdapter(ingredientAdapter);

        bt_addStep = findViewById(R.id.bt_addstep);
        bt_addStep.setVisibility(View.GONE);
        rv_steps = findViewById(R.id.rv_step);
        stepList = new ArrayList<>();
        stepAdapter = new StepAdapter(stepList);
        stepAdapter.setEditMode(false);
        rv_steps.setLayoutManager(new LinearLayoutManager(this));
        rv_steps.setAdapter(stepAdapter);
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
        executorService.execute(() -> {
            AppDatabase db = AppDatabaseInstance.getInstance(getApplicationContext());
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

        ingredientAdapter.setEditMode(true);
        stepAdapter.setEditMode(true);
    }

    private void saveRecipeUpdates() {
        String title = et_title.getText().toString();
        String category = sp_category.getSelectedItem().toString();

        if (title.isEmpty()) {
            et_title.setError("Title cannot be empty");
            return;
        }

        if (!validateRecipe()) {
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

        List<Ingredient> updatedIngredients = ingredientAdapter.getIngredients();
        List<Step> updatedSteps = stepAdapter.getSteps();

        executorService.execute(() -> {
            AppDatabase db = AppDatabaseInstance.getInstance(getApplicationContext());

            db.recipeDao().updateRecipeAndReplaceDetails(recipeDetails.recipe, updatedIngredients, updatedSteps);

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
