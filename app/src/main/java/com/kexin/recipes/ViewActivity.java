package com.kexin.recipes;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

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

    RecyclerView rv_ingredients;
    IngredientAdapter ingredientAdapter;
    List<Ingredient> ingredientList;

    RecyclerView rv_steps;
    StepAdapter stepAdapter;
    List<Step> stepList;

    HashMap<String, Integer> categoryImageMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

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
        bt_favorite = findViewById(R.id.bt_favorite);
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

        long recipeId = getIntent().getIntExtra("id", -1);
        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        }
    }

    private void loadRecipeDetails(long recipeId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "recipes").build();

            RecipeWithDetail recipeWithDetail = db.recipeDao().getRecipeWithDetails(recipeId);

            runOnUiThread(() -> {
                et_title.setText(recipeWithDetail.recipe.getTitle());
                sp_category.setSelection(getCategoryPosition(recipeWithDetail.recipe.getCategory()));
                iv_thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(recipeWithDetail.recipe.getThumbnail(), 0, recipeWithDetail.recipe.getThumbnail().length));

                ingredientAdapter.setIngredients(recipeWithDetail.ingredients);
                stepAdapter.setSteps(recipeWithDetail.steps);
            });
        });
    }

    private int getCategoryPosition(String category) {
        String[] categories = getResources().getStringArray(R.array.category_array);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return i;
            }
        }
        return 0;
    }
}
