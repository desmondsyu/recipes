package com.kexin.recipes.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.kexin.recipes.MainActivity;
import com.kexin.recipes.R;
import com.kexin.recipes.dao.RecipeDAO;
import com.kexin.recipes.db.AppDatabase;
import com.kexin.recipes.models.Ingredient;
import com.kexin.recipes.models.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private final List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumbnail;
        private final TextView tvTitle;
        private final ImageButton btFavorite;

        public ViewHolder(View view) {
            super(view);
            ivThumbnail = view.findViewById(R.id.iv_thumbnail);
            tvTitle = view.findViewById(R.id.tv_title);
            btFavorite = view.findViewById(R.id.bt_favorite);
        }

        public void bind(Recipe recipe) {
            tvTitle.setText(recipe.getTitle());

            if (recipe.getThumbnail() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(recipe.getThumbnail(), 0, recipe.getThumbnail().length);
                ivThumbnail.setImageBitmap(bitmap);
            } else {
                ivThumbnail.setImageResource(R.drawable.default_thumbnail);
            }

            btFavorite.setImageResource(recipe.getIsFavorite() ? R.drawable.heart_true : R.drawable.heart_false);

            btFavorite.setOnClickListener(v -> {
                recipe.setIsFavorite(!recipe.getIsFavorite());

                btFavorite.setImageResource(recipe.getIsFavorite() ? R.drawable.heart_true : R.drawable.heart_false);
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.widget_recipe, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Recipe recipe = recipeList.get(position);
        viewHolder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


}
