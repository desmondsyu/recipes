package com.kexin.recipes.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWithDetail {
    @Embedded
    public Recipe recipe;
    @Relation(
            parentColumn = "id",
            entityColumn = "recipe_id"
    )
    public List<Ingredient> ingredients;
    @Relation(
            parentColumn = "id",
            entityColumn = "recipe_id"
    )
    public List<Step> steps;
}
