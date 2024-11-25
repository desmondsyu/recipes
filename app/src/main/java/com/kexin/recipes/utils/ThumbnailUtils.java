package com.kexin.recipes.utils;

import android.widget.ImageView;

import com.kexin.recipes.R;

import java.util.HashMap;
import java.util.Map;

public class ThumbnailUtils {
    private static final Map<String, Integer> categoryImageMap = new HashMap<>();

    static {
        categoryImageMap.put("Appetizer", R.drawable.appetizer);
        categoryImageMap.put("Beverage", R.drawable.beverage);
        categoryImageMap.put("Breakfast", R.drawable.breakfast);
        categoryImageMap.put("Dessert", R.drawable.dessert);
        categoryImageMap.put("Main Course", R.drawable.main_course);
        categoryImageMap.put("Sauce", R.drawable.sauce);
        categoryImageMap.put("Side Dish", R.drawable.side_dish);
        categoryImageMap.put("Snacks", R.drawable.snacks);
        categoryImageMap.put("Soup", R.drawable.soup);
    }

    public static int getCategoryImage(String category) {
        return categoryImageMap.getOrDefault(category, R.drawable.default_thumbnail);
    }

    public static void setCategoryThumbnail(ImageView imageView, String category) {
        imageView.setImageResource(getCategoryImage(category));
    }
}
