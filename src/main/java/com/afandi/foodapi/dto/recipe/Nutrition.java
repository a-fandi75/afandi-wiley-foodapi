package com.afandi.foodapi.dto.recipe;

import java.util.List;

public class Nutrition {

    private List<Nutrient> nutrients;
    private List<Ingredient> ingredients;

    public List<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
