package com.afandi.foodapi.service.integration.spoonacular;

import com.afandi.foodapi.dto.recipe.Ingredient;
import com.afandi.foodapi.dto.recipe.Nutrition;

import java.util.List;

/**
 * Helper class where Spoonacular's API helper methods live.
 */
public class SpoonacularAPIHelper {

    private SpoonacularAPIHelper() { }

    /**
     * Iterate over ingredients of a <code>Nutrition</code> object, and sum up all calories nutrient among them.
     * @param nutrition <code>Nutrition</code> object to calculate calories for.
     * @param excludedIngredients list of ingredients to exclude from calories calculation.
     * @return total number of calories in a <code>Nutrition</code> object.
     */
    public static double calculateRecipeCalories(Nutrition nutrition, List<String> excludedIngredients) {
        if (nutrition.getIngredients() == null || nutrition.getIngredients().isEmpty()) return -1;

        List<Ingredient> ingredients = nutrition.getIngredients();

        if (excludedIngredients != null && !excludedIngredients.isEmpty()) {
            ingredients.removeIf(i -> excludedIngredients.contains(i.getName()));
        }

        return ingredients.stream()
            .map(i -> i.getNutrients().stream().filter(n -> n.getName().equals("Calories")).findFirst().get().getAmount())
            .mapToDouble(i -> i)
            .sum();
    }
}
