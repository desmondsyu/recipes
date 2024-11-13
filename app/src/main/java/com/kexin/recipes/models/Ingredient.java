package com.kexin.recipes.models;

public class Ingredient {
    private Integer id;
    private String quantity;
    private String unit;
    private String name;

    public Ingredient() {
    }

    public Ingredient(Integer id, String quantity, String unit, String name) {
        this.id = id;
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
