package com.kexin.recipes.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Step {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "recipe_id")
    private Integer recipeId;

    public Step() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }
}
