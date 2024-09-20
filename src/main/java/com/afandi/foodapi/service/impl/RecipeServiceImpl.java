package com.afandi.foodapi.service.impl;

import com.afandi.foodapi.controller.ResourceNotFoundException;
import com.afandi.foodapi.dto.CaloriesResponse;
import com.afandi.foodapi.dto.recipe.Nutrition;
import com.afandi.foodapi.dto.recipe.Recipe;
import com.afandi.foodapi.service.RecipeService;
import com.afandi.foodapi.service.integration.spoonacular.SpoonacularAPI;
import com.afandi.foodapi.service.integration.spoonacular.SpoonacularAPIConstants;
import com.afandi.foodapi.service.integration.spoonacular.SpoonacularAPIHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final SpoonacularAPI spoonacularAPI;

    public RecipeServiceImpl(SpoonacularAPI spoonacularAPI) {
        this.spoonacularAPI = spoonacularAPI;
    }

    public Mono<List<Recipe>> search(Map<String, String> params) {
        return spoonacularAPI.search(params);
    }

    public Mono<CaloriesResponse> getRecipeCalories(String name, List<String> excludedIngredients) {
        Map<String, String> params = new HashMap<>();
        params.put(SpoonacularAPIConstants.PARAM_TITLE_MATCH, name);
        params.put(SpoonacularAPIConstants.PARAM_ADD_RECIPE_INFO, "true");
        params.put(SpoonacularAPIConstants.PARAM_ADD_RECIPE_NUTRITION, "true");
        params.put(SpoonacularAPIConstants.PARAM_NUMBER, "1");

        Mono<List<Recipe>> result = spoonacularAPI.search(params);
        return result.flatMap(recipes -> {
            if (recipes == null || recipes.isEmpty()) {
                CaloriesResponse response = new CaloriesResponse();
                response.setError(String.format("No result was found for the search term: %s", name));
                return Mono.just(response);
            }
            return getRecipeCalories(recipes.get(0).getNutrition(), excludedIngredients);
        });
    }

    public Mono<CaloriesResponse> getRecipeCalories(long id, List<String> excludedIngredients) {
        return spoonacularAPI.nutritionById(id)
            .flatMap(n -> getRecipeCalories(n, excludedIngredients))
            .onErrorResume(
                WebClientResponseException.NotFound.class,
                E -> Mono.error(new ResourceNotFoundException(id))
            );
    }

    public Mono<CaloriesResponse> getRecipeCalories(Nutrition nutrition, List<String> excludedIngredients) {
        CaloriesResponse response = new CaloriesResponse();
        double calories = SpoonacularAPIHelper.calculateRecipeCalories(nutrition, excludedIngredients);
        if (calories < 0) {
            response.setError("Unable to calculate total calories for this recipe!");
        }
        else {
            response.setTotalCalories(calories);
        }
        return Mono.just(response);
    }
}
