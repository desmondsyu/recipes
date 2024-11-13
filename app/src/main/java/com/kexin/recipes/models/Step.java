package com.kexin.recipes.models;

public class Step {
    private Integer id;
    private String description;
    private Integer order;

    public Step() {
    }

    public Step(Integer id, String description, Integer order) {
        this.id = id;
        this.description = description;
        this.order = order;
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
