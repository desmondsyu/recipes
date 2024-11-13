package com.kexin.recipes.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kexin.recipes.R;
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
        private final Button btFavorite;

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
                ivThumbnail.setImageResource(R.drawable.default_thumbnail); // Set a default image if thumbnail is null
            }

            btFavorite.setText(recipe.getIsFavorite() ? "♥" : "♡");

            btFavorite.setOnClickListener(v -> {
                recipe.setIsFavorite(!recipe.getIsFavorite());
                btFavorite.setText(recipe.getIsFavorite() ? "♥" : "♡");
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
