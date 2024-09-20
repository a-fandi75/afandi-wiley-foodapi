package com.afandi.foodapi.service;

import com.afandi.foodapi.dto.CaloriesResponse;
import com.afandi.foodapi.dto.recipe.Nutrition;
import com.afandi.foodapi.dto.recipe.Recipe;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface RecipeService {

    /**
     * Query Spoonacular API for recipes, based on the provided <code>params</code>.
     */
    Mono<List<Recipe>> search(Map<String, String> params);

    /**
     * Search recipes by name, and retrieves a single recipe to calculate calories for.
     * @param name String the name that must be found in the title of the recipes.
     * @param excludedIngredients List of ingredients that needs to be excluded from calories calculation.
     * @return Mono object contains the <code>CaloriesResponse</code> object.
     */
    Mono<CaloriesResponse> getRecipeCalories(String name, List<String> excludedIngredients);

    /**
     * Search recipes by id, and retrieves the target recipe to calculate calories for.
     * @param id <code>long</code> targeted recipe id.
     * @param excludedIngredients List of ingredients that needs to be excluded from calories calculation.
     * @return Mono object contains the <code>CaloriesResponse</code> object.
     * @throws WebClientResponseException.NotFound if recourse with the specified <code>id</code> was not found.
     */
    Mono<CaloriesResponse> getRecipeCalories(long id, List<String> excludedIngredients);

    /**
     * Calculate the calories of the ingredients contained within <code>nutrition</code>.
     * @param nutrition <code>Nutrition</code> object that contains ingredients nutrition information.
     * @param excludedIngredients List of ingredients that needs to be excluded from calories calculation.
     * @return Mono object contains the <code>CaloriesResponse</code> object.
     */
    Mono<CaloriesResponse> getRecipeCalories(Nutrition nutrition, List<String> excludedIngredients);
}
