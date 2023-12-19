package com.example.helpme.UI.Model;

public class Order {
    private String helperName;
    private String helperExpType;
    private float rating;

    public Order() {
    }

    public Order(String helperName, String helperExpType, float rating) {
        this.helperName = helperName;
        this.helperExpType = helperExpType;
        this.rating = rating;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperExpType() {
        return helperExpType;
    }

    public void setHelperExpType(String helperExpType) {
        this.helperExpType = helperExpType;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
