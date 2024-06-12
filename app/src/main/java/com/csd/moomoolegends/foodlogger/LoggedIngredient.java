package com.csd.moomoolegends.foodlogger;

public class LoggedIngredient {
    private String displayName;
    private double quantity;
    private double footprint;

    private String category;



    public LoggedIngredient(String displayName, double quantity, double footprint) {
        this.displayName = displayName;
        this.quantity = quantity;
        this.footprint = footprint;
        this.category = "";
    }

    // Getters and Setters
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getFootprint() {
        return footprint;
    }

    public void setFootprint(double footprint) {
        this.footprint = footprint;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}