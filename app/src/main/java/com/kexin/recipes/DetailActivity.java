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
import androidx.room.Room;

import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Recipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    EditText et_title;
    Spinner sp_category;
    ImageView iv_thumbnail;
    Boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

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

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "recipes").build();

                db.recipeDao().insert(recipe);

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
}
